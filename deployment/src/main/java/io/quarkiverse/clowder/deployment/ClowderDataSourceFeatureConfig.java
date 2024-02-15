package io.quarkiverse.clowder.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface ClowderDataSourceFeatureConfig extends ClowderFeatureConfig {
    /**
     * Allow to overwrite the datasource port of the clowder config.
     */
    Optional<Integer> port();
}
