package io.quarkiverse.clowder.deployment;

import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface ClowderKafkaFeatureConfig extends ClowderFeatureConfig {
    /**
     * Allow to overwrite the kafka port of the clowder config.
     */
    Optional<Integer> port();

    /**
     * When configuring a channel like
     * `mp.messaging.incoming.prices.topic=${clowder.kafka.topics."platform.notifications.ingress".name}`
     * In case the topic "platform.notifications.ingress" does not exist in Clowder configuration, then it will use
     * "platform.notifications.ingress" as topic name.
     */
    @WithDefault("true")
    boolean useTopicNameFromProperty();

    /**
     * Topics configuration.
     */
    @ConfigDocSection
    Map<String, ClowderKafkaTopicFeatureConfig> topics();

}
