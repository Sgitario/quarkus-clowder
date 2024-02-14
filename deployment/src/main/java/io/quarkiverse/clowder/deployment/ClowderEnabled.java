package io.quarkiverse.clowder.deployment;

import java.util.function.BooleanSupplier;

public class ClowderEnabled implements BooleanSupplier {

    private final ClowderConfig config;

    public ClowderEnabled(ClowderConfig config) {
        this.config = config;
    }

    @Override
    public boolean getAsBoolean() {
        return config.enabled();
    }
}
