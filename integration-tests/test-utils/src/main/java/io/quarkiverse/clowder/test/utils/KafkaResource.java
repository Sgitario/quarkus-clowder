package io.quarkiverse.clowder.test.utils;

import static io.quarkiverse.clowder.utils.ConfigUtils.CLOWDER_KAFKA_PORT;

import java.util.Map;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class KafkaResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName IMAGE = DockerImageName.parse("confluentinc/cp-kafka:6.2.1");

    private final KafkaContainer container = new KafkaContainer(IMAGE);

    @Override
    public Map<String, String> start() {
        container.start();
        return Map.of(CLOWDER_KAFKA_PORT, "" + container.getMappedPort(9093));
    }

    @Override
    public void stop() {
        container.stop();
    }
}
