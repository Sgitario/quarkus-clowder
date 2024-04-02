package io.quarkiverse.clowder.test.utils;

import static io.quarkiverse.clowder.utils.ConfigUtils.CLOWDER_DATASOURCE_PORT;

import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PostgresResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName IMAGE = DockerImageName.parse("quay.io/centos7/postgresql-13-centos7:centos7")
            .asCompatibleSubstituteFor("postgres");
    private static final String USERNAME = "aUser";
    private static final String PASSWORD = "secret";
    private static final String DATABASE = "some-db";

    private final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(IMAGE)
            .waitingFor(Wait.forLogMessage(".*Starting server.*", 1))
            .withCommand("run-postgresql")
            // this configuration must match with the provided in cdappconfig.json:
            .withDatabaseName(DATABASE)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withEnv("POSTGRESQL_DATABASE", DATABASE)
            .withEnv("POSTGRESQL_USER", USERNAME)
            .withEnv("POSTGRESQL_PASSWORD", PASSWORD);

    @Override
    public Map<String, String> start() {
        container.start();
        return Map.of(CLOWDER_DATASOURCE_PORT, "" + container.getMappedPort(5432));
    }

    @Override
    public void stop() {
        container.stop();
    }
}
