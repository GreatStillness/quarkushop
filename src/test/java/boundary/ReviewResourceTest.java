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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
class ReviewResourceTest {

    @Test
    void testFindAllByProduct() {
        given().when()
                .get("/reviews/product/1")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(2))
                .body(containsString("id"))
                .body(containsString("title"))
                .body(containsString("rating"))
                .body(containsString("description"));
    }

    @Test
    void testFindById() {
        given().when()
                .get("/reviews/product/2")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("id"))
                .body(containsString("title"))
                .body(containsString("rating"))
                .body(containsString("description"));
    }

    @Test
    void testCreate() {
        Integer count = given().when()
                .get("/reviews/product/3")
                .then()
                .extract()
                .body()
                .path("size()");

        var requestParams = new HashMap<>();
        requestParams.put("description", "Wonderful laptop !");
        requestParams.put("rating", 5);
        requestParams.put("title", "Must have for every developer");

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/reviews/product/3")
                .then()
                .statusCode(OK.getStatusCode())
                .body(containsString("id"))
                .body(containsString("Wonderful laptop !"));

        given().when()
                .get("/reviews/product/3")
                .then()
                .body("size()", is(count + 1));
    }

    @Test
    void testDelete() {
        Integer count = given().when()
                .get("/reviews/product/2")
                .then()
                .extract()
                .body()
                .path("size()");

        given().when()
                .delete("/reviews/3")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .get("/reviews/product/2")
                .then()
                .body("size()", is(count - 1));
    }

    @Test
    void testReviewsDeletedWhenProductIsDeleted() {
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

        requestParams = new HashMap<>();
        requestParams.put("description", "Wonderful laptop !");
        requestParams.put("rating", 5);
        requestParams.put("title", "Must have for every developer");

        given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/reviews/product/" + newProductID)
                .then()
                .statusCode(OK.getStatusCode());

        given().when()
                .delete("/products/" + newProductID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        given().when()
                .get("/reviews/product/" + newProductID)
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", is(0));
    }
}
