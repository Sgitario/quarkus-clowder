package io.quarkiverse.clowder.sources;

import static io.quarkiverse.clowder.utils.CertUtils.createTempCertFile;
import static io.quarkiverse.clowder.utils.ConfigUtils.CLOWDER_DATASOURCE_PORT;
import static io.quarkiverse.clowder.utils.ConfigUtils.QUARKUS_DATASOURCE;
import static io.quarkiverse.clowder.utils.ConfigUtils.getOptionalApplicationProperty;

import java.util.HashMap;
import java.util.Map;

import io.quarkiverse.clowder.model.Database;

public abstract class DataSourceQuarkusClowderPropertyConfigSource extends QuarkusClowderPropertyConfigSource {

    public static final String POSTGRESQL = "postgresql";
    private static final String USERNAME = QUARKUS_DATASOURCE + "username";
    private static final String PASSWORD = QUARKUS_DATASOURCE + "password";

    DataSourceQuarkusClowderPropertyConfigSource(String name, Map<String, String> propertyMap) {
        super(name, load(propertyMap));
    }

    protected static Database getDatabaseModel() {
        return getModel().database;
    }

    protected static String getHostPortDb() {
        var database = getDatabaseModel();
        var port = getOptionalApplicationProperty(CLOWDER_DATASOURCE_PORT)
                .map(Integer::parseInt);
        return String.format("%s://%s:%d/%s",
                POSTGRESQL,
                database.hostname,
                port.orElse(database.port),
                database.name);
    }

    protected static String createTempRdsCertFile() {
        var certData = getDatabaseModel().rdsCa;
        if (certData != null) {
            return createTempCertFile("rds-ca-root", certData);
        } else {
            throw new IllegalStateException(
                    "'database.sslMode' is set to 'verify-full' in the Clowder config but the 'database.rdsCa' field is missing");
        }
    }

    private static Map<String, String> load(Map<String, String> propertyMap) {
        var clowder = getModel();
        Map<String, String> props = new HashMap<>(propertyMap);
        props.put(USERNAME, clowder.database.username);
        props.put(PASSWORD, clowder.database.password);
        return props;
    }
}
