package io.quarkiverse.clowder.deployment;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface ClowderFeatureConfig {
    /**
     * Enable/Disable the clowder feature.
     */
    @WithDefault("true")
    boolean enabled();
}
