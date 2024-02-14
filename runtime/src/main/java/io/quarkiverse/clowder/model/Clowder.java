package io.quarkiverse.clowder.model;

import java.util.List;

public class Clowder {

    public Database database;
    public List<Endpoint> endpoints;
    public List<PrivateEndpoint> privateEndpoints;
    public Kafka kafka;
    public Logging logging;
    public String metricsPath;
    public Integer metricsPort;
    public Integer privatePort;
    public Integer publicPort;
    public Integer webPort;
    public String tlsCAPath;
}
