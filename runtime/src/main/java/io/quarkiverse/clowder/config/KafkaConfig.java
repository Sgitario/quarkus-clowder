package io.quarkiverse.clowder.config;

import java.util.Map;
import java.util.Optional;

public class KafkaConfig {
    public Optional<Integer> port;
    public boolean useTopicNameFromProperty;
    public Map<String, KafkaTopicConfig> topics;
}
