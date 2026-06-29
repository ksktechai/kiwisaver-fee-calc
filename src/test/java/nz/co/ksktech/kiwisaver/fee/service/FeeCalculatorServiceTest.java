package nz.co.ksktech.kiwisaver.fee.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class FeeCalculatorServiceTest {

    private final FeeCalculatorService service = new FeeCalculatorService();

    @Test
    public void testCalculateWithKnownInputs() {
        // Fund: 1% fee, return 2%, balance=1000, contribute=500 for 3 years
        // Year 1: balance=(1000+500)=1500, growth=1500*0.02=30 -> 1530, fee=1530*0.01=15.30 -> 1514.70
        // Year 2: balance=(1514.70+500)=2014.70, growth=2014.70*0.02=40.29 -> 2054.99, fee=2054.99*0.01=20.55 -> 2034.44
        // Year 3: balance=(2034.44+500)=2534.44, growth=2534.44*0.02=50.69 -> 2585.13, fee=2585.13*0.01=25.85 -> 2559.28
        // Total fees = 15.30 + 20.55 + 25.85 = 61.70
        // Zero-fee: balance=(1000+500)=1500, growth=1500*0.02=30 -> 1530
        //           balance=(1530+500)=2030, growth=2030*0.02=40.60 -> 2070.60
        //           balance=(2070.60+500)=2570.60, growth=2570.60*0.02=51.41 -> 2622.01

        var fund = new nz.co.ksktech.kiwisaver.fee.model.Fund("test-1", "Test Fund", "Balanced",
                new BigDecimal("1.00"));

        var result = service.calculate(fund, 1000, 500, 3, 2.0);

        assertEquals("Test Fund", result.fund());
        assertNotNull(result.projectedBalance());
        assertNotNull(result.totalFeesPaid());
        assertNotNull(result.balanceWithoutFees());

        // Verify fees are positive
        assertTrue(result.totalFeesPaid().compareTo(BigDecimal.ZERO) > 0,
                "totalFeesPaid should be > 0");

        // Verify fees drag the balance down
        assertTrue(result.balanceWithoutFees().compareTo(result.projectedBalance()) > 0,
                "balanceWithoutFees should exceed projectedBalance due to fees");

        // Expected projected (hand-checkable): ~2559.28
        assertEquals("2559.28", result.projectedBalance().toPlainString(), "projected balance mismatch");

        // Expected total fees paid: ~61.70
        assertEquals("61.70", result.totalFeesPaid().toPlainString(), "total fees paid mismatch");

        // Expected zero-fee balance: ~2622.01
        assertEquals("2622.01", result.balanceWithoutFees().toPlainString(), "zero-fee balance mismatch");
    }

    @Test
    public void testCalculateZeroFeeFund() {
        var fund = new nz.co.ksktech.kiwisaver.fee.model.Fund("no-fee", "No Fee Fund", "Growth", BigDecimal.ZERO);
        var result = service.calculate(fund, 1000, 500, 2, 3.0);

        assertEquals("No Fee Fund", result.fund());
        assertEquals(BigDecimal.ZERO, result.totalFeesPaid().setScale(0), "Fees should be zero for a no-fee fund");
         // With zero fees, projected and without-fees should match exactly
         assertEquals(0, result.projectedBalance().compareTo(result.balanceWithoutFees()),
                "Projected and zero-fee balance should be equal when fee is 0");
    }
}
