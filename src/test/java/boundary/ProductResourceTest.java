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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class ProductResourceTest {

    @Test
    void testFindAll() {
        given().when()
                .get("/products")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThan(0))
                .body(containsString("name"))
                .body(containsString("description"))
                .body(containsString("price"))
                .body(containsString("categoryId"));
    }

    @Test
    void testFindById() {
        given().when()
                .get("/products/3")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("MacBook Pro 13"))
                .body(containsString("1999.00"))
                .body(containsString("categoryId"))
                .body(containsString("AVAILABLE"));
    }

    @Test
    void testCreate() {
        var count = given().when()
                .get("/products/count")
                .then()
                .extract()
                .body()
                .as(Long.class);

        var requestParams = new HashMap<>();
        requestParams.put("name", "Dell G5");
        requestParams.put("description", "Best gaming laptop from Dell");
        requestParams.put("price", 1490);
        requestParams.put("status", "AVAILABLE");
        requestParams.put("salesCounter", 0);
        requestParams.put("categoryId", 2);

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/products")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("id"))
                .body(containsString("Dell G5"));

        given().when()
                .get("/products/count")
                .then()
                .body(containsString(String.valueOf(count + 1)));
    }

    @Test
    void testDelete() {
        var requestParams = new HashMap<>();
        requestParams.put("name", "Dell G5");
        requestParams.put("description", "Best gaming laptop from Dell");
        requestParams.put("price", 1490);
        requestParams.put("status", "AVAILABLE");
        requestParams.put("salesCounter", 0);
        requestParams.put("categoryId", 2);

        var response = given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/products")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("");

        assertThat(response.get("id")).isNotNull();

        var newProductID = (Integer) response.get("id");

        given().when()
                .delete("/products/" + newProductID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .get("/products/" + newProductID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode())
                .body(is(emptyString()));
    }

    @Test
    void testFindByCategoryId() {
        var ids = given().when()
                .get("/products/category/1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getList("id", Long.class);

        assertThat(ids.size()).isEqualTo(3);
    }

    @Test
    void testCountByCategoryId() {
        var count = given().when()
                .get("/products/count/category/1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .as(Integer.class);

        assertThat(count).isGreaterThanOrEqualTo(2);
    }
}
