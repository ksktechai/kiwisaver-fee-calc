package nz.co.ksktech.kiwisaver.fee.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class CalculateResourceTest {

    private final String validPayload = """
            {
                "fundId": "balanced-1",
                "balance": 50000,
                "annualContribution": 6000,
                "years": 30,
                "annualReturnPercent": 5.0
            }
            """;

    @Test
    public void testCalculateReturnsSuccess() {
        given()
            .contentType(ContentType.JSON)
            .body(validPayload)
            .when().post("/calculate")
            .then()
            .statusCode(200)
             .body("fund", equalTo("KiwiSaver Balanced Fund"))
             .body("projectedBalance", notNullValue())
             .body("totalFeesPaid", notNullValue())
             .body("balanceWithoutFees", notNullValue());
    }

    @Test
    public void testCalculateFeesDragBalanceDown() {
        var res = given()
            .contentType(ContentType.JSON)
            .body(validPayload)
            .when().post("/calculate")
            .then()
            .statusCode(200)
            .extract().jsonPath();

        double projectedBalance = res.getDouble("projectedBalance");
        double totalFeesPaid = res.getDouble("totalFeesPaid");
        double balanceWithoutFees = res.getDouble("balanceWithoutFees");

        // Fees must be positive
        assert totalFeesPaid > 0 : "Total fees paid should be greater than zero";

        // Zero-fee balance must be strictly higher (fees drag it down)
        assert balanceWithoutFees > projectedBalance :
            String.format("balanceWithoutFees (%f) should be > projectedBalance (%f)", balanceWithoutFees, projectedBalance);
    }

    @Test
    public void testCalculateReturns404ForUnknownFund() {
        String payload = """
                {
                    "fundId": "nonexistent",
                    "balance": 10000,
                    "annualContribution": 1000,
                    "years": 5,
                    "annualReturnPercent": 4.0
                }
                """;

        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .when().post("/calculate")
            .then()
            .statusCode(404);
    }
}
