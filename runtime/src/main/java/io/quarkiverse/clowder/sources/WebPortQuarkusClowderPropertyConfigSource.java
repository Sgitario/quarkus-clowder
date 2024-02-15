package io.quarkiverse.clowder.sources;

import java.util.Map;

import io.quarkiverse.clowder.model.Clowder;

public class WebPortQuarkusClowderPropertyConfigSource extends QuarkusClowderPropertyConfigSource {

    private static final String QUARKUS_HTTP_PORT = "quarkus.http.port";
    private static final String QUARKUS_HTTP_TEST_PORT = "quarkus.http.test-port";

    public WebPortQuarkusClowderPropertyConfigSource(Clowder model) {
        super(WebPortQuarkusClowderPropertyConfigSource.class.getSimpleName(), load(model));
    }

    private static Map<String, String> load(Clowder clowder) {
        if (clowder.webPort == null) {
            return Map.of();
        }

        return Map.of(QUARKUS_HTTP_PORT, clowder.webPort.toString(),
                QUARKUS_HTTP_TEST_PORT, clowder.webPort.toString());
    }
}
