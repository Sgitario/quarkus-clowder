package io.quarkiverse.clowder.sources;

import java.util.Map;

import io.quarkiverse.clowder.ClowderRecorder;
import io.quarkiverse.clowder.model.Clowder;
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

    protected static Clowder getModel() {
        return ClowderRecorder.model;
    }
}
