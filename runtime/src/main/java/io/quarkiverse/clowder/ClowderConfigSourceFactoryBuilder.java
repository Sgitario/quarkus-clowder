package io.quarkiverse.clowder;

import static io.quarkiverse.clowder.sources.DataSourceQuarkusClowderPropertyConfigSource.POSTGRESQL;
import static io.quarkiverse.clowder.utils.ConfigUtils.HIBERNATE_ORM;
import static io.quarkiverse.clowder.utils.ConfigUtils.HIBERNATE_REACTIVE;
import static io.quarkiverse.clowder.utils.ConfigUtils.KAFKA;
import static io.quarkiverse.clowder.utils.ConfigUtils.QUARKUS_DATASOURCE_DB_KIND;
import static io.quarkiverse.clowder.utils.ConfigUtils.QUARKUS_MICROMETER_ENABLED;
import static io.quarkiverse.clowder.utils.ConfigUtils.QUARKUS_MICROMETER_EXPORT_PROMETHEUS_ENABLED;
import static io.quarkiverse.clowder.utils.ConfigUtils.REST_CLIENT;
import static io.quarkiverse.clowder.utils.ConfigUtils.REST_CLIENT_REACTIVE;
import static io.quarkiverse.clowder.utils.ConfigUtils.getBooleanApplicationProperty;
import static io.quarkiverse.clowder.utils.ConfigUtils.getOptionalApplicationProperty;
import static io.quarkiverse.clowder.utils.ConfigUtils.isCapabilityPresent;

import java.io.File;
import java.io.IOException;

import org.jboss.logging.Logger;

import io.quarkiverse.clowder.sources.ClowderJsonConfigSource;
import io.quarkiverse.clowder.sources.EndpointsQuarkusClowderPropertyConfigSource;
import io.quarkiverse.clowder.sources.JdbcDataSourceQuarkusClowderPropertyConfigSource;
import io.quarkiverse.clowder.sources.KafkaQuarkusClowderPropertyConfigSource;
import io.quarkiverse.clowder.sources.MetricsQuarkusClowderPropertyConfigSource;
import io.quarkiverse.clowder.sources.ReactiveDataSourceQuarkusClowderPropertyConfigSource;
import io.quarkiverse.clowder.sources.WebPortQuarkusClowderPropertyConfigSource;
import io.quarkus.runtime.configuration.ConfigBuilder;
import io.smallrye.config.SmallRyeConfigBuilder;

public class ClowderConfigSourceFactoryBuilder implements ConfigBuilder {

    private static final Logger LOGGER = Logger.getLogger(ClowderConfigSourceFactoryBuilder.class);

    @Override
    public SmallRyeConfigBuilder configBuilder(SmallRyeConfigBuilder builder) {
        if (isFeatureEnabled(ClowderFeature.CONFIG)) {
            builder = builder.withSources(clowderConfig());
        }
        if (isFeatureEnabled(ClowderFeature.WEB)) {
            builder = builder.withSources(clowderWebPort());
        }
        if (isFeatureEnabled(ClowderFeature.KAFKA) && isCapabilityPresent(KAFKA)) {
            builder = builder.withSources(clowderKafka());
        }
        if (isFeatureEnabled(ClowderFeature.DATASOURCE)
                && isClowderDataSourceConfigured()
                && isQuarkusDbKindCompatibleWithClowder()) {
            if (isCapabilityPresent(HIBERNATE_REACTIVE)) {
                builder = builder.withSources(clowderReactiveDataSource());
            } else if (isCapabilityPresent(HIBERNATE_ORM)) {
                builder = builder.withSources(clowderJdbcDataSource());
            }
        }
        if (isFeatureEnabled(ClowderFeature.METRICS) && isMicrometerPrometheusEnabled() && isClowderMetricsConfigured()) {
            builder = builder.withSources(clowderMetrics());
        }
        if (isCapabilityPresent(REST_CLIENT) || isCapabilityPresent(REST_CLIENT_REACTIVE)) {
            builder = builder.withSources(clowderEndpoints());
        }

        return builder;
    }

    private boolean isMicrometerPrometheusEnabled() {
        return getBooleanApplicationProperty(QUARKUS_MICROMETER_ENABLED)
                && getBooleanApplicationProperty(QUARKUS_MICROMETER_EXPORT_PROMETHEUS_ENABLED);
    }

    private boolean isClowderMetricsConfigured() {
        return ClowderRecorder.model.metricsPath != null && ClowderRecorder.model.metricsPort != null;
    }

    private boolean isFeatureEnabled(ClowderFeature clowderFeature) {
        return ClowderRecorder.rootConfig.enabledFeatures.contains(clowderFeature);
    }

    private boolean isQuarkusDbKindCompatibleWithClowder() {
        var dbKind = getOptionalApplicationProperty(QUARKUS_DATASOURCE_DB_KIND);
        if (dbKind.isEmpty()) {
            // quarkus-agroal is not configured, we dismiss the datasource configuration for clowder.
            return false;
        } else if (!POSTGRESQL.equals(dbKind.get())) {
            LOGGER.warnf("The `{}` property was `{}` which is not supported by the clowder environment. " +
                    "Clowder won't configure the Quarkus datasource.",
                    QUARKUS_DATASOURCE_DB_KIND,
                    dbKind.get());
            return false;
        }

        return true;
    }

    private boolean isClowderDataSourceConfigured() {
        return ClowderRecorder.model.database != null;
    }

    private static ClowderJsonConfigSource clowderConfig() {
        try {
            return new ClowderJsonConfigSource(ClowderRecorder.rootConfig.prefix,
                    new File(ClowderRecorder.rootConfig.configPath).toURI().toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static WebPortQuarkusClowderPropertyConfigSource clowderWebPort() {
        return new WebPortQuarkusClowderPropertyConfigSource();
    }

    private static JdbcDataSourceQuarkusClowderPropertyConfigSource clowderJdbcDataSource() {
        return new JdbcDataSourceQuarkusClowderPropertyConfigSource();
    }

    private static ReactiveDataSourceQuarkusClowderPropertyConfigSource clowderReactiveDataSource() {
        return new ReactiveDataSourceQuarkusClowderPropertyConfigSource();
    }

    private static KafkaQuarkusClowderPropertyConfigSource clowderKafka() {
        return new KafkaQuarkusClowderPropertyConfigSource();
    }

    private static MetricsQuarkusClowderPropertyConfigSource clowderMetrics() {
        return new MetricsQuarkusClowderPropertyConfigSource();
    }

    private static EndpointsQuarkusClowderPropertyConfigSource clowderEndpoints() {
        return new EndpointsQuarkusClowderPropertyConfigSource();
    }
}
