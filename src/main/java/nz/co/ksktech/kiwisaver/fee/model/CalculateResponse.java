package nz.co.ksktech.kiwisaver.fee.model;

import java.math.BigDecimal;

public record CalculateResponse(String fund, BigDecimal projectedBalance, BigDecimal totalFeesPaid,
                                 BigDecimal balanceWithoutFees) {
}
