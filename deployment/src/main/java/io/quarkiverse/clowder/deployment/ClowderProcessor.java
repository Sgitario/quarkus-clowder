package io.quarkiverse.clowder.deployment;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.fabric8.openshift.api.model.Template;
import io.quarkiverse.clowder.ClowderConfigSourceFactoryBuilder;
import io.quarkiverse.clowder.ClowderFeature;
import io.quarkiverse.clowder.ClowderRecorder;
import io.quarkiverse.clowder.config.EndpointConfig;
import io.quarkiverse.clowder.config.KafkaConfig;
import io.quarkiverse.clowder.config.KafkaTopicConfig;
import io.quarkiverse.clowder.config.RootConfig;
import io.quarkiverse.clowder.deployment.resources.EnvNameClowdAppResourceDecorator;
import io.quarkiverse.clowder.deployment.resources.ImageDeploymentClowdAppResourceDecorator;
import io.quarkiverse.clowder.deployment.resources.InitializeClowdAppResourceDecorator;
import io.quarkiverse.clowder.deployment.resources.InitializeTemplateResourceDecorator;
import io.quarkiverse.clowder.deployment.resources.LabelsClowdAppResourceDecorator;
import io.quarkiverse.clowder.deployment.resources.ReplicasDeploymentClowdAppResourceDecorator;
import io.quarkiverse.clowder.deployment.resources.ResourceDecorator;
import io.quarkiverse.clowder.deployment.resources.ResourceDecoratorContext;
import io.quarkiverse.clowder.deployment.utils.ClowderResourcesSerializer;
import io.quarkus.container.spi.ContainerImageInfoBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ApplicationInfoBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedFileSystemResourceBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigBuilderBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;
import io.quarkus.kubernetes.spi.GeneratedKubernetesResourceBuildItem;

public class ClowderProcessor {

    private static final String FEATURE_NAME = "clowder";
    /**
     * The order of the decorators DOES matter.
     */
    private static final List<ResourceDecorator> DECORATORS = List.of(new InitializeTemplateResourceDecorator(),
            new InitializeClowdAppResourceDecorator(),
            new EnvNameClowdAppResourceDecorator(),
            new LabelsClowdAppResourceDecorator(),
            new ReplicasDeploymentClowdAppResourceDecorator(),
            new ImageDeploymentClowdAppResourceDecorator());

    @BuildStep(onlyIf = ClowderEnabled.class)
    void feature(BuildProducer<FeatureBuildItem> feature,
            BuildProducer<RunTimeConfigBuilderBuildItem> runTimeConfigBuilder) {
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
        runTimeConfigBuilder.produce(new RunTimeConfigBuilderBuildItem(ClowderConfigSourceFactoryBuilder.class));
    }

    @BuildStep(onlyIf = ClowderEnabled.class)
    void generateResources(ClowderConfig config,
            ApplicationInfoBuildItem applicationInfo,
            OutputTargetBuildItem outputTarget,
            Optional<ContainerImageInfoBuildItem> imageInfo,
            BuildProducer<GeneratedKubernetesResourceBuildItem> kubernetesFiles,
            BuildProducer<GeneratedFileSystemResourceBuildItem> generatedFiles) {
        if (!config.resources().enabled()) {
            return;
        }

        var image = config.resources().image()
                .or(() -> imageInfo.map(ContainerImageInfoBuildItem::getImage));
        if (image.isEmpty()) {
            throw new IllegalStateException("No container-image has been configured. You need to either configure " +
                    "any of the Quarkus Container Image extensions or provide one using the " +
                    "property `quarkus.clowder.resource.image`");
        }

        Path inputFile = getInputFile(config);
        Path outputFile = getOutputFile(config, outputTarget);

        ResourceDecoratorContext context = new ResourceDecoratorContext(config, applicationInfo, image.get());
        Template template = ClowderResourcesSerializer.read(inputFile);
        for (ResourceDecorator decorator : DECORATORS) {
            decorator.decorate(template, context);
        }

        String content = ClowderResourcesSerializer.write(outputFile, template);
        registerGeneratedResources(outputFile, content, kubernetesFiles, generatedFiles);
    }

    @BuildStep(onlyIf = ClowderEnabled.class)
    @Record(ExecutionTime.STATIC_INIT)
    void init(Capabilities capabilities, ClowderConfig config, ClowderRecorder recorder) {
        RootConfig rootConfig = new RootConfig();
        rootConfig.enabledFeatures = Stream.of(ClowderFeature.values())
                .filter(isEnabledInConfig(config))
                .collect(Collectors.toUnmodifiableSet());
        rootConfig.prefix = config.prefix();
        rootConfig.configPath = config.configPath();
        rootConfig.endpoints = new HashMap<>();
        for (var endpoint : config.endpoints().entrySet()) {
            var endpointConfig = new EndpointConfig();
            endpointConfig.client = endpoint.getValue().client();
            endpointConfig.hostname = endpoint.getValue().hostname();
            endpointConfig.port = endpoint.getValue().port();
            rootConfig.endpoints.put(endpoint.getKey(), endpointConfig);
        }
        rootConfig.kafkaConfig = new KafkaConfig();
        rootConfig.kafkaConfig.port = config.kafka().port();
        rootConfig.kafkaConfig.useTopicNameFromProperty = config.kafka().useTopicNameFromProperty();
        for (var topicEntry : config.kafka().topics().entrySet()) {
            var topicConfig = new KafkaTopicConfig();
            topicConfig.name = topicEntry.getValue().name();
            rootConfig.kafkaConfig.topics.put(topicEntry.getKey(), topicConfig);
        }

        recorder.initialize(capabilities.getCapabilities(), rootConfig);
    }

    private Predicate<ClowderFeature> isEnabledInConfig(ClowderConfig config) {
        // the clowder feature is enabled if it's not configured or it's explicitly set to false.
        return f -> switch (f) {
            case WEB -> config.web().enabled();
            case CONFIG -> config.config().enabled();
            case DATASOURCE -> config.datasource().enabled();
            case KAFKA -> config.kafka().enabled();
            case METRICS -> config.metrics().enabled();
            case RESOURCES -> config.resources().enabled();
        };
    }

    private Path getInputFile(ClowderConfig config) {
        return Paths.get(config.resources().inputFile());
    }

    private Path getOutputFile(ClowderConfig config, OutputTargetBuildItem outputTarget) {
        Path path = Paths.get(config.resources().outputFile());
        if (!path.isAbsolute()) {
            return outputTarget.getOutputDirectory().resolve(path);
        }

        return path;
    }

    private void registerGeneratedResources(Path outputFile, String content,
            BuildProducer<GeneratedKubernetesResourceBuildItem> kubernetesFiles,
            BuildProducer<GeneratedFileSystemResourceBuildItem> generatedFiles) {
        String filename = outputFile.toAbsolutePath().toString();
        var contentInBytes = content.getBytes(StandardCharsets.UTF_8);
        kubernetesFiles.produce(new GeneratedKubernetesResourceBuildItem(filename, contentInBytes));
        generatedFiles.produce(new GeneratedFileSystemResourceBuildItem(filename, contentInBytes));
    }
}
