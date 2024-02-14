package io.quarkiverse.clowder.deployment;

import io.quarkiverse.clowder.ClowderConfigSourceFactoryBuilder;
import io.quarkiverse.clowder.ClowderRecorder;
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
    void setup(ClowderConfig config, ClowderRecorder recorder) {
        recorder.loadClowder(config.prefix(), config.configPath());
    }
}
