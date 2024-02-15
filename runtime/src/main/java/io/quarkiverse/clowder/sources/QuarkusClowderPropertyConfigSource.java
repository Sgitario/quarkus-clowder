package io.quarkiverse.clowder.sources;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.configuration.ConfigUtils;
import io.smallrye.config.common.MapBackedConfigSource;

public abstract class QuarkusClowderPropertyConfigSource extends MapBackedConfigSource {

    QuarkusClowderPropertyConfigSource(String name, Map<String, String> propertyMap) {
        super(name, propertyMap);
    }

    @Override
    public int getOrdinal() {
        // Provide a value higher than 250 to it overrides application.properties
        return 270;
    }

    protected static Optional<String> getOptionalApplicationProperty(String key) {
        return ConfigUtils.getFirstOptionalValue(List.of(key), String.class);
    }
}
