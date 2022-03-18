package boundary;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import utils.TestContainerResource;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class CategoryResourceTest {

    @Test
    void testFindAll() {
        get("/categories").then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(2))
                .body(containsString("Phones & Smartphones"))
                .body(containsString("Mobile"))
                .body(containsString("Computers and Laptops"))
                .body(containsString("PC"));
    }

    @Test
    void testFindProductsByCategoryId() {
        get("/categories/1/products").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("categoryId"))
                .body(containsString("description"))
                .body(containsString("id"))
                .body(containsString("name"))
                .body(containsString("price"))
                .body(containsString("reviews"))
                .body(containsString("salesCounter"))
                .body(containsString("status"));
    }

    @Test
    void testCreate() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Cars");
        requestParams.put("description", "New and used cars");

        var response = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/categories")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertNotNull(response.get("id"));
    }

    @Test
    void testDelete() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Home");
        requestParams.put("description", "New and old homes");

        var response = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/categories")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("");

        assertThat(response).isNotNull();
        assertThat(response.get("id")).isNotNull();
        assertNotNull(response.get("id"));

        var newProductID = (Integer) response.get("id");

        given().when()
                .delete("/categories/" + newProductID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());
    }
}
