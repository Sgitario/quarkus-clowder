package io.quarkiverse.clowder.deployment.resources;

import io.quarkiverse.clowder.deployment.ClowderConfig;
import io.quarkus.deployment.builditem.ApplicationInfoBuildItem;

public record ResourceDecoratorContext(ClowderConfig config, ApplicationInfoBuildItem applicationInfo, String image) {
}
