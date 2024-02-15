package io.quarkiverse.clowder.sources;

import static io.quarkiverse.clowder.utils.ConfigUtils.QUARKUS_DATASOURCE;

import java.util.HashMap;
import java.util.Map;

public class ReactiveDataSourceQuarkusClowderPropertyConfigSource extends DataSourceQuarkusClowderPropertyConfigSource {
    private static final String QUARKUS_DATASOURCE_REACTIVE = QUARKUS_DATASOURCE + "reactive.";
    private static final String REACTIVE_URL = QUARKUS_DATASOURCE_REACTIVE + "url";
    private static final String REACTIVE_POSTGRESQL_SSL_MODE = QUARKUS_DATASOURCE_REACTIVE + POSTGRESQL + ".ssl-mode";
    private static final String HOSTNAME_VERIFICATION_ALGORITHM = QUARKUS_DATASOURCE_REACTIVE
            + "hostname-verification-algorithm";
    private static final String TRUST_CERTIFICATE_PEM = QUARKUS_DATASOURCE_REACTIVE + "reactive.trust-certificate-pem";
    private static final String TRUST_CERTIFICATE_PEM_CERTS = TRUST_CERTIFICATE_PEM + ".certs";

    public ReactiveDataSourceQuarkusClowderPropertyConfigSource() {
        super(ReactiveDataSourceQuarkusClowderPropertyConfigSource.class.getSimpleName(), load());
    }

    private static Map<String, String> load() {
        var database = getDatabaseModel();

        Map<String, String> props = new HashMap<>();
        props.put(REACTIVE_URL, getHostPortDb());
        if (database.isSslModeEnabled()) {
            props.put(REACTIVE_POSTGRESQL_SSL_MODE, database.sslMode);
        }
        if (database.isSslModeVerifyFull()) {
            props.put(HOSTNAME_VERIFICATION_ALGORITHM, "HTTPS");
            props.put(TRUST_CERTIFICATE_PEM, Boolean.TRUE.toString());
            props.put(TRUST_CERTIFICATE_PEM_CERTS, createTempRdsCertFile());
        }

        return props;
    }
}
