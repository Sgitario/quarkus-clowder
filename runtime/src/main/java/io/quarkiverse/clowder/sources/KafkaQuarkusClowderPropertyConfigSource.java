package io.quarkiverse.clowder.sources;

import static io.quarkiverse.clowder.utils.CertUtils.createTempCertFile;
import static io.quarkiverse.clowder.utils.ConfigUtils.CLOWDER_KAFKA_PORT;
import static io.quarkiverse.clowder.utils.ConfigUtils.getApplicationProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.quarkiverse.clowder.ClowderRecorder;
import io.quarkiverse.clowder.model.Broker;
import io.quarkiverse.clowder.utils.ConfigUtils;

public class KafkaQuarkusClowderPropertyConfigSource extends QuarkusClowderPropertyConfigSource {

    private static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private static final String KAFKA_SASL_JAAS_CONFIG_KEY = "kafka.sasl.jaas.config";
    private static final String KAFKA_SASL_MECHANISM_KEY = "kafka.sasl.mechanism";
    private static final String KAFKA_SECURITY_PROTOCOL_KEY = "kafka.security.protocol";
    private static final String KAFKA_SSL_TRUSTSTORE_LOCATION_KEY = "kafka.ssl.truststore.location";
    private static final String KAFKA_SSL_TRUSTSTORE_TYPE_KEY = "kafka.ssl.truststore.type";
    public static final String KAFKA_SSL_TRUSTSTORE_TYPE_VALUE = "PEM";
    private static final String CLOWDER_KAFKA_TOPICS = "clowder.kafka.topics.";
    private static final String CLOWDER_KAFKA_TOPIC_NAME = ".name";

    public KafkaQuarkusClowderPropertyConfigSource() {
        super(KafkaQuarkusClowderPropertyConfigSource.class.getSimpleName(), load());
    }

    @Override
    public String getValue(String propertyName) {
        if (ClowderRecorder.rootConfig.kafkaConfig.useTopicNameFromProperty
                && propertyName.startsWith(CLOWDER_KAFKA_TOPICS)) {
            // it was already resolved in the load() method:
            var topicName = getProperties().get(propertyName);
            if (topicName == null) {
                // if it was not resolved in the load() method, we use the key from the property
                topicName = extractTopicNameFromProperty(propertyName);
            }

            return topicName;
        }

        return super.getValue(propertyName);
    }

    private String extractTopicNameFromProperty(String propertyName) {
        int firstPart = propertyName.indexOf(CLOWDER_KAFKA_TOPICS);
        int lastPart = propertyName.lastIndexOf(CLOWDER_KAFKA_TOPIC_NAME);
        return propertyName.substring(firstPart + CLOWDER_KAFKA_TOPICS.length(), lastPart)
                .replaceAll(Pattern.quote("\""), "");

    }

    private static Map<String, String> load() {
        var clowder = getModel();
        if (clowder.kafka == null) {
            return Map.of();
        }

        Broker saslBroker = null;
        Broker sslBroker = null;
        var port = ConfigUtils.getOptionalApplicationProperty(CLOWDER_KAFKA_PORT)
                .map(Integer::parseInt);
        StringBuilder sb = new StringBuilder();
        for (var broker : clowder.kafka.brokers) {
            if (!sb.isEmpty()) {
                sb.append(',');
            }
            sb.append(broker.hostname).append(":").append(port.orElse(broker.port));
            if (saslBroker == null && "sasl".equals(broker.authtype)) {
                saslBroker = broker;
            }

            if (sslBroker == null && "SSL".equals(broker.securityProtocol)) {
                sslBroker = broker;
            }
        }

        Map<String, String> props = new HashMap<>();
        props.put(KAFKA_BOOTSTRAP_SERVERS, sb.toString());
        if (clowder.kafka.topics != null) {
            for (var topic : clowder.kafka.topics) {
                props.put(CLOWDER_KAFKA_TOPICS + "\"" + topic.requestedName + "\"" + CLOWDER_KAFKA_TOPIC_NAME, topic.name);
            }
        }

        if (saslBroker != null) {
            props.put(KAFKA_SECURITY_PROTOCOL_KEY, saslBroker.sasl.securityProtocol);
            props.put(KAFKA_SSL_TRUSTSTORE_LOCATION_KEY, createTempKafkaCertFile(saslBroker.cacert));
            props.put(KAFKA_SSL_TRUSTSTORE_TYPE_KEY,
                    getApplicationProperty(KAFKA_SSL_TRUSTSTORE_TYPE_KEY, KAFKA_SSL_TRUSTSTORE_TYPE_VALUE));

            props.put(KAFKA_SASL_JAAS_CONFIG_KEY, loadSaslJaasConfigKey(saslBroker));
            props.put(KAFKA_SASL_MECHANISM_KEY, saslBroker.sasl.saslMechanism);
        } else if (sslBroker != null) {
            props.put(KAFKA_SECURITY_PROTOCOL_KEY, sslBroker.securityProtocol);
            props.put(KAFKA_SSL_TRUSTSTORE_LOCATION_KEY, createTempKafkaCertFile(sslBroker.cacert));
            props.put(KAFKA_SSL_TRUSTSTORE_TYPE_KEY,
                    getApplicationProperty(KAFKA_SSL_TRUSTSTORE_TYPE_KEY, KAFKA_SSL_TRUSTSTORE_TYPE_VALUE));
        }

        return props;
    }

    private static String loadSaslJaasConfigKey(Broker saslBroker) {
        String username = saslBroker.sasl.username;
        String password = saslBroker.sasl.password;
        return switch (saslBroker.sasl.saslMechanism) {
            case "PLAIN" -> "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username
                    + "\" password=\"" + password + "\";";
            case "SCRAM-SHA-512" ->
                "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + username
                        + "\" password=\"" + password + "\";";
            default -> null;
        };

    }

    private static String createTempKafkaCertFile(String certData) {
        return certData != null ? createTempCertFile("kafka-cacert", certData) : null;
    }
}
