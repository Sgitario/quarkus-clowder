package io.quarkiverse.clowder.sources;

import java.util.Map;

public class WebPortQuarkusClowderPropertyConfigSource extends QuarkusClowderPropertyConfigSource {

    private static final String QUARKUS_HTTP_PORT = "quarkus.http.port";

    public WebPortQuarkusClowderPropertyConfigSource() {
        super(WebPortQuarkusClowderPropertyConfigSource.class.getSimpleName(), load());
    }

    private static Map<String, String> load() {
        var clowder = getModel();
        if (clowder.webPort == null) {
            return Map.of();
        }

        return Map.of(QUARKUS_HTTP_PORT, clowder.webPort.toString());
    }
}
