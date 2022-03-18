package boundary;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import org.greatstillness.enums.PaymentStatus;
import org.junit.jupiter.api.Test;
import utils.TestContainerResource;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisabledOnNativeImage
@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class PaymentResourceTest {

    @Test
    void testFindAll() {
        var payments = given().when()
                .get("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList("");

        assertThat(payments).isNotNull();
    }

    @Test
    void testFindById() {
        var response = given().when()
                .get("/payments/4")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("");

        assertEquals(4, response.get("id"));
        assertEquals(ACCEPTED.name(), response.get("status"));
        assertEquals("paymentId", response.get("paypalPaymentId"));
        assertEquals(5, response.get("orderId"));
    }

    @Test
    void testCreate() {
        var requestParams = new HashMap<>();

        requestParams.put("orderId", 4);
        requestParams.put("paypalPaymentId", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        var response = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("");

        var createdPaymentId = (Integer) response.get("id");
        assertThat(createdPaymentId).isNotZero();
        assertThat(response).containsEntry("orderId", 4)
                .containsEntry("paypalPaymentId", "anotherPaymentId")
                .containsEntry("status", PaymentStatus.PENDING.name());

        given().when()
                .delete("/payments/" + createdPaymentId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDelete() {
        var requestParams = new HashMap<>();

        requestParams.put("orderId", 4);
        requestParams.put("transaction", "anotherPaymentId");
        requestParams.put("status", PaymentStatus.PENDING);

        var createdPaymentId = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/payments")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        given().when()
                .delete("/payments/" + createdPaymentId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
}
