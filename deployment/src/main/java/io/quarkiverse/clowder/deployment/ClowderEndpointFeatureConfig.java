package io.quarkiverse.clowder.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface ClowderEndpointFeatureConfig extends ClowderFeatureConfig {
    /**
     * Fully qualified class matching the rest-client interface to configure.
     */
    Optional<String> client();

    /**
     * Allow to overwrite the hostname for the current endpoint.
     */
    Optional<String> hostname();

    /**
     * Allow to overwrite the port for the current endpoint.
     */
    Optional<Integer> port();
}
