package io.quarkiverse.clowder;

import java.io.IOException;

import io.quarkiverse.clowder.utils.ClowderJsonConfigSource;
import io.quarkiverse.clowder.utils.QuarkusClowderWebPortPropertyConfigSource;
import io.quarkus.runtime.configuration.ConfigBuilder;
import io.smallrye.config.SmallRyeConfigBuilder;

public class ClowderConfigSourceFactoryBuilder implements ConfigBuilder {
    @Override
    public SmallRyeConfigBuilder configBuilder(SmallRyeConfigBuilder builder) {
        return builder.withSources(clowderConfig(), clowderWebPort());
    }

    private static ClowderJsonConfigSource clowderConfig() {
        try {
            return new ClowderJsonConfigSource(ClowderRecorder.clowderPrefix,
                    ClowderRecorder.clowderConfigFile.toURI().toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static QuarkusClowderWebPortPropertyConfigSource clowderWebPort() {
        return new QuarkusClowderWebPortPropertyConfigSource(ClowderRecorder.model);
    }
}
