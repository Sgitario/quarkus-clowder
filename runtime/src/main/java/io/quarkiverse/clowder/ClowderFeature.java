package io.quarkiverse.clowder;

public enum ClowderFeature {
    /**
     * Feature to inject the clowder configuration as Quarkus config source.
     */
    CONFIG,
    /**
     * Feature to configure the web ports using the Clowder configuration.
     */
    WEB,
    /**
     * Feature to configure the data sources using the Clowder configuration.
     */
    DATASOURCE;
}
