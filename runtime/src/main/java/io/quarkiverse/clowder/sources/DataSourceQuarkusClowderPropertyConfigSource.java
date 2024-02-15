package io.quarkiverse.clowder.sources;

import static io.quarkiverse.clowder.utils.CertUtils.createTempCertFile;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;

import io.quarkiverse.clowder.model.Clowder;
import io.quarkiverse.clowder.model.Database;

public class DataSourceQuarkusClowderPropertyConfigSource extends QuarkusClowderPropertyConfigSource {

    private static final Logger LOGGER = Logger.getLogger(DataSourceQuarkusClowderPropertyConfigSource.class);

    private static final String POSTGRESQL = "postgresql";
    private static final String QUARKUS_DATASOURCE = "quarkus.datasource.";
    private static final String DB_KIND = QUARKUS_DATASOURCE + "db-kind";
    private static final String JDBC_URL = QUARKUS_DATASOURCE + "jdbc.url";
    private static final String USERNAME = QUARKUS_DATASOURCE + "username";
    private static final String PASSWORD = QUARKUS_DATASOURCE + "password";
    private static final String QUARKUS_DATASOURCE_REACTIVE = QUARKUS_DATASOURCE + "reactive.";
    private static final String REACTIVE_URL = QUARKUS_DATASOURCE_REACTIVE + "url";
    private static final String REACTIVE_POSTGRESQL_SSL_MODE = QUARKUS_DATASOURCE_REACTIVE + POSTGRESQL + ".ssl-mode";
    private static final String HOSTNAME_VERIFICATION_ALGORITHM = QUARKUS_DATASOURCE_REACTIVE
            + "hostname-verification-algorithm";
    private static final String TRUST_CERTIFICATE_PEM = QUARKUS_DATASOURCE_REACTIVE + "reactive.trust-certificate-pem";
    private static final String TRUST_CERTIFICATE_PEM_CERTS = TRUST_CERTIFICATE_PEM + ".certs";

    public DataSourceQuarkusClowderPropertyConfigSource(Clowder model) {
        super(DataSourceQuarkusClowderPropertyConfigSource.class.getSimpleName(), load(model));
    }

    private static Map<String, String> load(Clowder clowder) {
        Map<String, String> props = new HashMap<>();
        if (clowder.database == null) {
            return props;
        }

        var dbKind = getOptionalApplicationProperty(DB_KIND);
        if (dbKind.isEmpty()) {
            // quarkus-agroal is not configured, we dismiss the datasource configuration for clowder.
            return props;
        }

        if (!POSTGRESQL.equals(dbKind.get())) {
            LOGGER.warnf("The `{}` property was `{}` which is not supported by the clowder environment.", DB_KIND,
                    dbKind.get());
            return props;
        }

        props.put(USERNAME, clowder.database.username);
        props.put(PASSWORD, clowder.database.password);
        props.put(JDBC_URL, buildJdbcUrl(clowder.database));
        props.put(REACTIVE_URL, getHostPortDb(clowder.database));
        if (clowder.database.isSslModeEnabled()) {
            props.put(REACTIVE_POSTGRESQL_SSL_MODE, clowder.database.sslMode);
        }
        if (clowder.database.isSslModeVerifyFull()) {
            props.put(HOSTNAME_VERIFICATION_ALGORITHM, "HTTPS");
            props.put(TRUST_CERTIFICATE_PEM, Boolean.TRUE.toString());
            props.put(TRUST_CERTIFICATE_PEM_CERTS, createTempRdsCertFile(clowder.database.rdsCa));
        }

        return props;
    }

    private static String buildJdbcUrl(Database database) {
        String hostPortDb = getHostPortDb(database);
        String tracing = "";
        var existingJdbcUrl = getOptionalApplicationProperty(JDBC_URL);
        if (existingJdbcUrl.isPresent()) {
            if (existingJdbcUrl.get().contains(":otel:")) {
                tracing = "otel:";
            }
        }

        String jdbcUrl = String.format("jdbc:%s%s", tracing, hostPortDb);
        if (database.isSslModeEnabled()) {
            jdbcUrl = jdbcUrl + "?sslmode=" + database.sslMode;
        }
        if (database.isSslModeVerifyFull()) {
            jdbcUrl = jdbcUrl + "&sslrootcert=" + createTempRdsCertFile(database.rdsCa);
        }

        return jdbcUrl;
    }

    private static String getHostPortDb(Database database) {
        return String.format("%s://%s:%d/%s",
                POSTGRESQL,
                database.hostname,
                database.port,
                database.name);
    }

    private static String createTempRdsCertFile(String certData) {
        if (certData != null) {
            return createTempCertFile("rds-ca-root", certData);
        } else {
            throw new IllegalStateException(
                    "'database.sslMode' is set to 'verify-full' in the Clowder config but the 'database.rdsCa' field is missing");
        }
    }
}
