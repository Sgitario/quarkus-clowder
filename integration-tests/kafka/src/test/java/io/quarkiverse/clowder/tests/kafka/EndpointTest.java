package io.quarkiverse.clowder.tests.kafka;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import io.quarkiverse.clowder.test.utils.KafkaResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@QuarkusTestResource(value = KafkaResource.class, restrictToAnnotatedClass = true)
class EndpointTest {
    @Test
    void testKafkaIntegration() {
        given().when().get("/prices/topic").then().statusCode(HttpStatus.SC_OK).body(is("platform-tmp-12345"));
        given().when().get("/priceOut/topic").then().statusCode(HttpStatus.SC_OK).body(is("platform-tmp-12345"));
        given().when().put("/prices/12.5").then().statusCode(HttpStatus.SC_NO_CONTENT);
        Awaitility.await()
                .untilAsserted(() -> {
                    given().when().get("/prices/first")
                            .then()
                            .statusCode(HttpStatus.SC_OK)
                            .body(is(String.valueOf(12.5)));
                });
    }
}
