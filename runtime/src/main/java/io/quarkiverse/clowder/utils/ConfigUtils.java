package io.quarkiverse.clowder.utils;

import java.util.List;
import java.util.Optional;

import io.quarkiverse.clowder.ClowderRecorder;

public class ConfigUtils {

    private static final String QUARKUS_PREFIX = "io.quarkus";
    public static final String HIBERNATE_REACTIVE = QUARKUS_PREFIX + ".hibernate.reactive";
    public static final String HIBERNATE_ORM = QUARKUS_PREFIX + ".hibernate.orm";
    public static final String QUARKUS_DATASOURCE = "quarkus.datasource.";
    public static final String QUARKUS_DATASOURCE_DB_KIND = QUARKUS_DATASOURCE + "db-kind";

    private ConfigUtils() {

    }

    public static Optional<String> getOptionalApplicationProperty(String key) {
        return io.quarkus.runtime.configuration.ConfigUtils.getFirstOptionalValue(List.of(key), String.class);
    }

    public static boolean isCapabilityPresent(String capability) {
        return ClowderRecorder.capabilities.contains(capability);
    }
}
