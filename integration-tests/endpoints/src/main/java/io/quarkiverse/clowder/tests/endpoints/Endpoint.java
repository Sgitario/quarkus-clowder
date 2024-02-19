package io.quarkiverse.clowder.tests.endpoints;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.openapi.quarkus.notifications_api_yaml.api.DefaultApi;

@Path("")
public class Endpoint {

    @ConfigProperty(name = "clowder.database.name")
    String databaseName;

    @RestClient
    @Inject
    DefaultApi client;

    @RestClient
    @Inject
    AnotherApi anotherApi;

    @GET
    @Path("/database/name")
    public String getDatabaseName() {
        return databaseName;
    }

    @GET
    @Path("/client/notifications/api/database/name")
    public String getDatabaseNameFromNotificationsApi() {
        return client.getDatabaseName().readEntity(String.class);
    }

    @GET
    @Path("/client/another/api/database/name")
    public String getDatabaseNameFromAnotherApi() {
        return anotherApi.getDatabaseName();
    }
}
