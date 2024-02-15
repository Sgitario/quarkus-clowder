package io.quarkiverse.clowder.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface ClowderKafkaFeatureConfig extends ClowderFeatureConfig {
    /**
     * Allow to overwrite the kafka port of the clowder config.
     */
    Optional<Integer> port();
}
