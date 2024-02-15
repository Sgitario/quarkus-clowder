package io.quarkiverse.clowder.sources;

import static io.quarkiverse.clowder.utils.ConfigUtils.CLOWDER_KAFKA_PORT;

import java.util.HashMap;
import java.util.Map;

import io.quarkiverse.clowder.utils.ConfigUtils;

public class KafkaQuarkusClowderPropertyConfigSource extends QuarkusClowderPropertyConfigSource {

    private static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private static final String CLOWDER_KAFKA_TOPIC = "clowder.kafka.topics.";

    public KafkaQuarkusClowderPropertyConfigSource() {
        super(KafkaQuarkusClowderPropertyConfigSource.class.getSimpleName(), load());
    }

    private static Map<String, String> load() {
        var clowder = getModel();
        if (clowder.kafka == null) {
            return Map.of();
        }

        var port = ConfigUtils.getOptionalApplicationProperty(CLOWDER_KAFKA_PORT)
                .map(Integer::parseInt);
        StringBuilder sb = new StringBuilder();
        for (var broker : clowder.kafka.brokers) {
            if (!sb.isEmpty()) {
                sb.append(',');
            }
            sb.append(broker.hostname).append(":").append(port.orElse(broker.port));
        }

        Map<String, String> props = new HashMap<>();
        props.put(KAFKA_BOOTSTRAP_SERVERS, sb.toString());
        for (var topic : clowder.kafka.topics) {
            props.put(CLOWDER_KAFKA_TOPIC + topic.requestedName, topic.name);
        }

        return props;
    }
}
