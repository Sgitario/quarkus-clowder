package io.quarkiverse.clowder.tests.endpoints;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/database/name")
public interface AnotherApi {

    @GET
    String getDatabaseName();
}
