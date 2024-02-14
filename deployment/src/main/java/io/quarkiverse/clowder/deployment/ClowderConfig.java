package io.quarkiverse.clowder.deployment;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.clowder")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface ClowderConfig {
    /**
     * Enable/Disable the clowder extension.
     */
    @WithDefault("true")
    boolean enabled();

    /**
     * Location of the clowder config file.
     * The default location is provided either from the property `acg.config` which
     * default path is `/cdapp/cdappconfig.json`.
     */
    @WithDefault("${acg.config:/cdapp/cdappconfig.json}")
    String configPath();

    /**
     * Prefix where all the properties found at the `quarkus.clowder.config-path` location can be found.
     */
    @WithDefault("clowder.")
    String prefix();
}
