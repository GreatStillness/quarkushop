package boundary;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import utils.TestContainerResource;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class CustomerResourceTest {

    @Test
    void testAll() {
        given().when()
                .get("/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThanOrEqualTo(3))
                .body(containsString("jason.bourne@mail.hello"))
                .body(containsString("homer.simpson@mail.hello"))
                .body(containsString("peter.quinn@mail.hello"));
    }

    @Test
    void testAllActiveUsers() {
        given().when()
                .get("/customers/active")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("Bourne"));
    }

    @Test
    void testAllInactiveUsers() {
        given().when()
                .get("/customers/inactive")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("peter.quinn@mail.hello"));
    }

    @Test
    void testFindById() {
        given().when()
                .get("/customers/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("Bourne"));
    }

    @Test
    void testCreate() {
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

        assertThat(newCustomerId).isNotZero();

        given().when()
                .get("/customers/" + newCustomerId)
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Saul"))
                .body(containsString("Berenson"))
                .body(containsString("call.saul@mail.com"));

        given().when()
                .delete("/customers/" + newCustomerId)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testDeleteThenCustomerIsDisabled() {
        var initialActiveCount = given().when()
                .get("/customers/active")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("size()");

        var initialInactiveCount = given().when()
                .get("/customers/inactive")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().jsonPath()
                .getInt("size()");

        given().when()
                .delete("/customers/2")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .get("/customers/active")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(initialActiveCount - 1));

        given().when()
                .get("/customers/inactive")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(initialInactiveCount + 1))
                .body(containsString("Peter"))
                .body(containsString("Quinn"));
    }
}
