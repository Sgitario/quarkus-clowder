package io.quarkiverse.clowder.sources;

import static io.quarkiverse.clowder.utils.ConfigUtils.QUARKUS_DATASOURCE;
import static io.quarkiverse.clowder.utils.ConfigUtils.getOptionalApplicationProperty;

import java.util.Map;

public class JdbcDataSourceQuarkusClowderPropertyConfigSource extends DataSourceQuarkusClowderPropertyConfigSource {
    private static final String JDBC_URL = QUARKUS_DATASOURCE + "jdbc.url";

    public JdbcDataSourceQuarkusClowderPropertyConfigSource() {
        super(JdbcDataSourceQuarkusClowderPropertyConfigSource.class.getSimpleName(), load());
    }

    private static Map<String, String> load() {
        return Map.of(JDBC_URL, buildJdbcUrl());
    }

    private static String buildJdbcUrl() {
        var database = getDatabaseModel();
        String hostPortDb = getHostPortDb();
        String tracing = "";
        var existingJdbcUrl = getOptionalApplicationProperty(JDBC_URL);
        if (existingJdbcUrl.isPresent() && existingJdbcUrl.get().contains(":otel:")) {
            tracing = "otel:";
        }

        String jdbcUrl = String.format("jdbc:%s%s", tracing, hostPortDb);
        if (database.isSslModeEnabled()) {
            jdbcUrl = jdbcUrl + "?sslmode=" + database.sslMode;
        }
        if (database.isSslModeVerifyFull()) {
            jdbcUrl = jdbcUrl + "&sslrootcert=" + createTempRdsCertFile();
        }

        return jdbcUrl;
    }
}
