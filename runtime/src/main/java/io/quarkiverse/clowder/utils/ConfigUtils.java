package io.quarkiverse.clowder.utils;

import java.util.List;
import java.util.Optional;

import io.quarkiverse.clowder.ClowderRecorder;

public class ConfigUtils {

    private static final String QUARKUS_PREFIX = "io.quarkus";
    public static final String KAFKA = QUARKUS_PREFIX + ".kafka";
    public static final String HIBERNATE_REACTIVE = QUARKUS_PREFIX + ".hibernate.reactive";
    public static final String HIBERNATE_ORM = QUARKUS_PREFIX + ".hibernate.orm";
    public static final String REST_CLIENT = QUARKUS_PREFIX + ".rest.client";
    public static final String REST_CLIENT_REACTIVE = REST_CLIENT + ".reactive";
    public static final String QUARKUS_DATASOURCE = "quarkus.datasource.";
    public static final String QUARKUS_DATASOURCE_DB_KIND = QUARKUS_DATASOURCE + "db-kind";
    public static final String QUARKUS_MICROMETER_ENABLED = "quarkus.micrometer.enabled";
    public static final String QUARKUS_MICROMETER_EXPORT_PROMETHEUS_ENABLED = "quarkus.micrometer.export.prometheus.enabled";
    public static final String CLOWDER_DATASOURCE_PORT = "quarkus.clowder.datasource.port";
    public static final String CLOWDER_KAFKA_PORT = "quarkus.clowder.kafka.port";
    public static final String OPENAPI_CLASS = "io.quarkiverse.openapi.generator.OpenApiGeneratorConfig";

    private ConfigUtils() {

    }

    public static boolean getBooleanApplicationProperty(String key) {
        return getOptionalApplicationProperty(key).map(Boolean::parseBoolean).orElse(false);
    }

    public static String getApplicationProperty(String key, String defaultValue) {
        return io.quarkus.runtime.configuration.ConfigUtils.getFirstOptionalValue(List.of(key), String.class)
                .orElse(defaultValue);
    }

    public static Optional<String> getOptionalApplicationProperty(String key) {
        return io.quarkus.runtime.configuration.ConfigUtils.getFirstOptionalValue(List.of(key), String.class);
    }

    public static boolean isCapabilityPresent(String capability) {
        return ClowderRecorder.capabilities.contains(capability);
    }

    public static boolean isOpenApiGeneratorPresent() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            cl.loadClass(OPENAPI_CLASS);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
