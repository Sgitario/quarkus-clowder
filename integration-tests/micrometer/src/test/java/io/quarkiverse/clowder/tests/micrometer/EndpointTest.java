package io.quarkiverse.clowder.tests.micrometer;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class EndpointTest {
    @Test
    void testMetrics() {
        given().port(9001).when().get("/metrics").then().statusCode(HttpStatus.SC_OK);
    }
}
