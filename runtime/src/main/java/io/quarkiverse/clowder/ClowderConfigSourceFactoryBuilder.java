package io.quarkiverse.clowder;

import java.io.IOException;

import io.quarkiverse.clowder.utils.ClowderJsonConfigSource;
import io.quarkus.runtime.configuration.ConfigBuilder;
import io.smallrye.config.SmallRyeConfigBuilder;

public class ClowderConfigSourceFactoryBuilder implements ConfigBuilder {
    @Override
    public SmallRyeConfigBuilder configBuilder(SmallRyeConfigBuilder builder) {
        try {
            return builder.withSources(new ClowderJsonConfigSource(ClowderRecorder.clowderPrefix,
                    ClowderRecorder.clowderConfigFile.toURI().toURL()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
