package io.quarkiverse.clowder.tests.minimal;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("")
public class Endpoint {

    @ConfigProperty(name = "clowder.database.name")
    String databaseName;

    @GET
    @Path("/database/name")
    public String getDatabaseName() {
        return databaseName;
    }
}
