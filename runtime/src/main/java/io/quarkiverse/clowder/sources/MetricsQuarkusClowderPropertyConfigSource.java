package io.quarkiverse.clowder.sources;

import java.util.Map;

import io.quarkiverse.clowder.utils.ConfigUtils;

public class MetricsQuarkusClowderPropertyConfigSource extends QuarkusClowderPropertyConfigSource {

    private static final String QUARKUS_MANAGEMENT_ENABLED = "quarkus.management.enabled";
    private static final String QUARKUS_MANAGEMENT_PORT = "quarkus.management.port";
    private static final String QUARKUS_MANAGEMENT_ROOT_PATH = "quarkus.management.root-path";
    private static final String MICROMETER_EXPORT_PROMETHEUS_PATH = "quarkus.micrometer.export.prometheus.path";

    public MetricsQuarkusClowderPropertyConfigSource() {
        super(MetricsQuarkusClowderPropertyConfigSource.class.getSimpleName(), load());
    }

    private static Map<String, String> load() {
        var clowder = getModel();

        var managementEnabled = ConfigUtils.getBooleanApplicationProperty(QUARKUS_MANAGEMENT_ENABLED);
        if (!managementEnabled) {
            throw new RuntimeException(
                    "The HTTP management port is not enabled and it's a requirement to configure Clowder metrics. " +
                            "Either enable the HTTP management port by using `" + QUARKUS_MANAGEMENT_ENABLED + "` or disable " +
                            "the Clowder metrics by using `quarkus.clowder.metrics.enabled=false`");
        }

        var managementPath = ConfigUtils.getOptionalApplicationProperty(QUARKUS_MANAGEMENT_ROOT_PATH);
        if (managementPath.isEmpty() || !managementPath.get().equals("/")) {
            throw new RuntimeException("The HTTP management is enabled, but using the default root path `/q`. To " +
                    "configure Clowder metrics, you need to configure the HTTP management root path with" +
                    "`" + QUARKUS_MANAGEMENT_ROOT_PATH + "=/` or disable " +
                    "the Clowder metrics by using `quarkus.clowder.metrics.enabled=false`");
        }

        var prometheusPath = ConfigUtils.getOptionalApplicationProperty(MICROMETER_EXPORT_PROMETHEUS_PATH);
        if (prometheusPath.isPresent() && !clowder.metricsPath.equals(prometheusPath.get())) {
            throw new RuntimeException(
                    "The micrometer prometheus path is using a different value than the one in clowder. To " +
                            "configure Clowder metrics, you need to configure the micrometer prometheus path with" +
                            "`" + MICROMETER_EXPORT_PROMETHEUS_PATH + "=" + clowder.metricsPath + "` or disable " +
                            "the Clowder metrics by using `quarkus.clowder.metrics.enabled=false`");
        }
        return Map.of(QUARKUS_MANAGEMENT_PORT, clowder.metricsPort.toString());
    }
}
