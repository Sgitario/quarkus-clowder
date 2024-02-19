package io.quarkiverse.clowder.sources;

import static io.quarkiverse.clowder.utils.ConfigUtils.getOptionalApplicationProperty;
import static io.quarkiverse.clowder.utils.ConfigUtils.isOpenApiGeneratorPresent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.logging.Logger;

import io.quarkiverse.clowder.ClowderRecorder;
import io.quarkiverse.clowder.config.EndpointConfig;
import io.quarkiverse.clowder.model.Endpoint;
import io.smallrye.config.common.utils.StringUtil;

public class EndpointsQuarkusClowderPropertyConfigSource extends QuarkusClowderPropertyConfigSource {
    private static final Logger LOGGER = Logger.getLogger(EndpointsQuarkusClowderPropertyConfigSource.class);

    private static final String CLIENT_PROPERTY = "quarkus.rest-client.\"%s\".";
    private static final String OPENAPI_GENERATOR_CONFIG_KEY = "quarkus.openapi-generator.codegen.spec.%s_%s.config-key";
    private static final List<String> OPENAPI_GENERATOR_FORMATS = List.of("yaml", "yml", "json");

    private static final Integer PORT_NOT_SET = 0;

    public EndpointsQuarkusClowderPropertyConfigSource() {
        super(EndpointsQuarkusClowderPropertyConfigSource.class.getSimpleName(), load());
    }

    private static Map<String, String> load() {
        var clowder = getModel();
        if (clowder.endpoints == null) {
            return Map.of();
        }

        Map<String, String> props = new HashMap<>();
        for (var endpoint : clowder.endpoints) {
            String endpointKey = getEndpointKey(endpoint);
            var endpointConfig = getEndpointConfig(endpointKey);
            String clientKey = getClientKey(endpointKey, endpointConfig);
            LOGGER.infof("Using the REST Client config key '%s' for the Clowder endpoint '%s'", clientKey,
                    endpointKey);
            String clientProperty = String.format(CLIENT_PROPERTY, clientKey);
            String hostname = getHostname(endpoint, endpointConfig);
            int port = getPort(endpoint, endpointConfig);
            if (usesTls(endpoint)) {
                props.put(clientProperty + "url", String.format("https://%s:%s", hostname, port));
                props.put(clientProperty + "trust-store-path", ClowderRecorder.getTrustStorePath());
                props.put(clientProperty + "trust-store-password", ClowderRecorder.getTrustStorePassword());
                props.put(clientProperty + "trust-store-type", ClowderRecorder.getTrustStoreType());
            } else {
                props.put(clientProperty + "url", String.format("http://%s:%s", hostname, port));
            }
        }

        return props;
    }

    private static String getHostname(Endpoint endpoint, Optional<EndpointConfig> endpointConfig) {
        if (endpointConfig.isPresent() && endpointConfig.get().hostname.isPresent()) {
            return endpointConfig.get().hostname.get();
        }

        return endpoint.hostname;
    }

    private static Integer getPort(Endpoint endpoint, Optional<EndpointConfig> endpointConfig) {
        if (endpointConfig.isPresent() && endpointConfig.get().port.isPresent()) {
            return endpointConfig.get().port.get();
        }

        return endpoint.port;
    }

    private static Optional<EndpointConfig> getEndpointConfig(String endpointKey) {
        return Optional.ofNullable(ClowderRecorder.rootConfig.endpoints.get(endpointKey));
    }

    private static String getEndpointKey(Endpoint endpoint) {
        return endpoint.app + "." + endpoint.name;
    }

    private static String getClientKey(String endpointKey, Optional<EndpointConfig> endpointConfig) {
        if (endpointConfig.isEmpty() || endpointConfig.get().client.isEmpty()) {
            if (isOpenApiGeneratorPresent()) {
                var clientKeyForOpenApi = OPENAPI_GENERATOR_FORMATS.stream()
                        .map(f -> getOptionalApplicationProperty(String.format(OPENAPI_GENERATOR_CONFIG_KEY, endpointKey, f)))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        // Best effort approach: using the default convention by openapi
                        .orElse(endpointKey + "_yaml");
                return StringUtil.replaceNonAlphanumericByUnderscores(clientKeyForOpenApi);
            }
        }

        return endpointConfig.get().client.get();
    }

    private static boolean usesTls(Endpoint endpoint) {
        return endpoint.tlsPort != null && !endpoint.tlsPort.equals(PORT_NOT_SET);
    }
}
