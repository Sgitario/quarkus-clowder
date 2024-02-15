package io.quarkiverse.clowder;

import java.io.IOException;

import io.quarkiverse.clowder.sources.ClowderJsonConfigSource;
import io.quarkiverse.clowder.sources.DataSourceQuarkusClowderPropertyConfigSource;
import io.quarkiverse.clowder.sources.WebPortQuarkusClowderPropertyConfigSource;
import io.quarkus.runtime.configuration.ConfigBuilder;
import io.smallrye.config.SmallRyeConfigBuilder;

public class ClowderConfigSourceFactoryBuilder implements ConfigBuilder {
    @Override
    public SmallRyeConfigBuilder configBuilder(SmallRyeConfigBuilder builder) {
        return builder.withSources(clowderConfig(), clowderWebPort(), clowderDataSource());
    }

    private static ClowderJsonConfigSource clowderConfig() {
        try {
            return new ClowderJsonConfigSource(ClowderRecorder.clowderPrefix,
                    ClowderRecorder.clowderConfigFile.toURI().toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static WebPortQuarkusClowderPropertyConfigSource clowderWebPort() {
        return new WebPortQuarkusClowderPropertyConfigSource(ClowderRecorder.model);
    }

    private static DataSourceQuarkusClowderPropertyConfigSource clowderDataSource() {
        return new DataSourceQuarkusClowderPropertyConfigSource(ClowderRecorder.model);
    }
}
