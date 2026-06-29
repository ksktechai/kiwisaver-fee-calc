package nz.co.ksktech.kiwisaver.fee.resource;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FundResource.class)
public class FundResourceTest {

     @Test
    public void testListFundsReturnsExpectedCount() {
        given()
                 .when().get("/funds")
                 .then()
                 .statusCode(200)
                 .body("$.size()", equalTo(5));
    }

     @Test
    public void testGetFundReturns200ForValidId() {
        Response resp = given()
                 .when().get("/funds/conservative-1")
                 .then()
                 .statusCode(200)
                 .extract().response();

         assertEquals("conservative-1", resp.jsonPath().getString("id"));
         assertEquals("KiwiSaver Conservative Fund", resp.jsonPath().getString("name"));
         assertEquals("Conservative", resp.jsonPath().getString("type"));
         assertEquals(0, doubleCompare(resp.jsonPath().getDouble("annualFeePercent"), 0.45), "annualFeePercent should be 0.45");
    }

     private int doubleCompare(double a, double b) {
        return Double.compare(a, b);
    }

     @Test
    public void testGetFundReturns404ForUnknownId() {
        given()
                 .when().get("/funds/nonexistent-id")
                 .then()
                 .statusCode(404);
    }
}
