package io.quarkiverse.clowder.config;

import java.util.Set;

import io.quarkiverse.clowder.ClowderFeature;

public class RootConfig {
    public String configPath;
    public String prefix;
    public Set<ClowderFeature> enabledFeatures;
}
