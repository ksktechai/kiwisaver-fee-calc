package nz.co.ksktech.kiwisaver.fee.service;

import io.quarkus.arc.DefaultBean;
import nz.co.ksktech.kiwisaver.fee.model.CalculateRequest;
import nz.co.ksktech.kiwisaver.fee.model.CalculateResponse;
import nz.co.ksktech.kiwisaver.fee.model.Fund;

import jakarta.enterprise.inject.Produces;
import java.math.BigDecimal;
import java.math.RoundingMode;

@jakarta.enterprise.context.ApplicationScoped
public class FeeCalculatorService {

    private static final int SCALE = 2;
    private static final RoundingMode RM = RoundingMode.HALF_UP;

    public CalculateResponse calculate(Fund fund, double balance, double annualContribution,
                                        int years, double annualReturnPercent) {
        BigDecimal currentBalance = BigDecimal.valueOf(balance);
        BigDecimal contribution = BigDecimal.valueOf(annualContribution);
        BigDecimal returnRate = BigDecimal.valueOf(annualReturnPercent)
                .divide(BigDecimal.valueOf(100), SCALE, RM);
        BigDecimal feeRate = fund.annualFeePercent()
                 .divide(BigDecimal.valueOf(100), SCALE, RM);
        BigDecimal cumulativeFees = BigDecimal.ZERO;

        for (int i = 0; i < years; i++) {
            // Add contribution at start of year
            currentBalance = currentBalance.add(contribution);

            // Apply growth return
            BigDecimal returnAmount = currentBalance.multiply(returnRate).setScale(SCALE, RM);
            currentBalance = currentBalance.add(returnAmount);

            // Deduct annual fee
            BigDecimal fee = currentBalance.multiply(feeRate).setScale(SCALE, RM);
            cumulativeFees = cumulativeFees.add(fee);
            currentBalance = currentBalance.subtract(fee);
        }

        // Compute zero-fee balance: run same simulation without fee deduction
        BigDecimal balanceWithoutFees = BigDecimal.valueOf(balance);
        BigDecimal zeroFeeCumulativeFees = BigDecimal.ZERO; // not used but tracks what would have been fees

        for (int i = 0; i < years; i++) {
            balanceWithoutFees = balanceWithoutFees.add(contribution);
            BigDecimal returnAmount = balanceWithoutFees.multiply(returnRate).setScale(SCALE, RM);
            balanceWithoutFees = balanceWithoutFees.add(returnAmount);
            // No fee deduction here
        }

        return new CalculateResponse(
                fund.name(),
                currentBalance.setScale(SCALE, RM),
                cumulativeFees.setScale(SCALE, RM),
                balanceWithoutFees.setScale(SCALE, RM)
        );
    }

    @Produces
    @jakarta.enterprise.context.Dependent
    public BigDecimal defaultDecimalContext() {
        return BigDecimal.ONE;
    }
}
