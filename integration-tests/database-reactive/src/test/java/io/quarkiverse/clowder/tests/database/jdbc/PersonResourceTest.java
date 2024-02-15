package io.quarkiverse.clowder.tests.database.jdbc;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import io.quarkiverse.clowder.test.utils.PostgresResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(value = PostgresResource.class, restrictToAnnotatedClass = true)
class PersonResourceTest {
    @Test
    void testDatasource() {
        given().when().get("/person")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
