package io.quarkiverse.clowder.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface ClowderKafkaTopicFeatureConfig {

    /**
     * Name of the topic to use.
     */
    Optional<String> name();
}
