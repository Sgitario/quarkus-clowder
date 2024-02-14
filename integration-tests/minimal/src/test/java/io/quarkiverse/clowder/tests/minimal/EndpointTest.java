package io.quarkiverse.clowder.tests.minimal;

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
    void testIntegrateClowderPropertiesToQuarkus() {
        given()
                .when().get("/database/name")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body(is("some-db"));
    }
}
