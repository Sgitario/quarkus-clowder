package io.quarkiverse.clowder.deployment;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.quarkiverse.clowder.ClowderConfigSourceFactoryBuilder;
import io.quarkiverse.clowder.ClowderFeature;
import io.quarkiverse.clowder.ClowderRecorder;
import io.quarkiverse.clowder.config.RootConfig;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigBuilderBuildItem;

public class ClowderProcessor {

    private static final String FEATURE_NAME = "clowder";

    @BuildStep(onlyIf = ClowderEnabled.class)
    void feature(BuildProducer<FeatureBuildItem> feature,
            BuildProducer<RunTimeConfigBuilderBuildItem> runTimeConfigBuilder) {
        feature.produce(new FeatureBuildItem(FEATURE_NAME));
        runTimeConfigBuilder.produce(new RunTimeConfigBuilderBuildItem(ClowderConfigSourceFactoryBuilder.class));
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
        };
    }
}
