package io.quarkiverse.clowder.tests.endpoints;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Quarkus Tests should be compatible with the Clowder extension.
 */
@QuarkusTest
class EndpointTest {
    @Test
    void testEndpoints() {
        given().when().get("/client/notifications/api/database/name")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(is("some-db"));

        given().when().get("/client/another/api/database/name")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(is("some-db"));
    }
}
