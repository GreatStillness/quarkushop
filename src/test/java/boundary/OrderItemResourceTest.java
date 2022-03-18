package boundary;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import utils.TestContainerResource;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class OrderItemResourceTest {

    @Test
    void testFindByOrderId() {
        given().when()
                .get("/order-items/order/1")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void testFindById() {
        var newCustomerId = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(Map.of("firstName", "Saul", "lastName", "Berenson", "email", "call.saul@mail.com"))
                .post("/customers").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        var newCartId = given().when()
                .post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        var cart = new HashMap<>();
        cart.put("id", newCartId);

        var requestParams = new HashMap<>();
        requestParams.put("cart", cart);

        var address = new HashMap<>();
        address.put("address1", "413 Circle Drive");
        address.put("city", "Washington, DC");
        address.put("country", "US");
        address.put("postcode", "20004");

        requestParams.put("shipmentAddress", address);

        var orderResponse = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/orders")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("");

        var newOrderId = (Integer) orderResponse.get("id");

        var orderItemRequestParams = new HashMap<>();
        orderItemRequestParams.put("quantity", 1);
        orderItemRequestParams.put("productId", 3);
        orderItemRequestParams.put("orderId", newOrderId);

        var orderItemResponse = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(orderItemRequestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("");

        var orderItemId = (Integer) orderItemResponse.get("id");

        given().when()
                .get("/order-items/" + orderItemId)
                .then()
                .statusCode(OK.getStatusCode());

        given().when()
                .delete("/orders/" + newOrderId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .delete("/carts/" + newCartId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testCreate() {
        var totalPrice = given().when()
                .get("/orders/4")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        var requestParams = new HashMap<>();
        requestParams.put("quantity", 1);
        requestParams.put("productId", 3);
        requestParams.put("orderId", 4);

        assertThat(totalPrice).isZero();

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode());

        var newTotalPrice = given().when()
                .get("/orders/4")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getDouble("totalPrice");

        assertThat(newTotalPrice).isEqualTo(totalPrice + 1999);
    }

    @Test
    void testDelete() {
        var requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        var newCustomerId = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        var newCartId = given().when()
                .post("/carts/customer/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        var cart = new HashMap<>();
        cart.put("id", newCartId);

        requestParams = new HashMap<>();
        requestParams.put("cart", cart);

        var address = new HashMap<>();
        address.put("address1", "413 Circle Drive");
        address.put("city", "Washington, DC");
        address.put("country", "US");
        address.put("postcode", "20004");

        requestParams.put("shipmentAddress", address);

        var orderResponse = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/orders")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("");

        var newOrderId = (Integer) orderResponse.get("id");

        var orderItemRequestParams = new HashMap<>();
        orderItemRequestParams.put("quantity", 1);
        orderItemRequestParams.put("productId", 3);
        orderItemRequestParams.put("orderId", newOrderId);

        var orderItemResponse = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(orderItemRequestParams)
                .post("/order-items/")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body()
                .jsonPath()
                .getMap("");

        var orderItemId = (Integer) orderItemResponse.get("id");

        given().when()
                .get("/order-items/" + orderItemId)
                .then()
                .statusCode(OK.getStatusCode());

        given().when()
                .delete("/order-items/" + orderItemId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .delete("/orders/" + newOrderId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .delete("/carts/" + newCartId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
}
