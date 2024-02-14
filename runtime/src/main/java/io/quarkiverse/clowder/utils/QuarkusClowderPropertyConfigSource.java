package io.quarkiverse.clowder.utils;

import java.util.Map;

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
}
