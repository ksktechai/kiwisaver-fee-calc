package nz.co.ksktech.kiwisaver.fee.model;

import java.math.BigDecimal;

public record Fund(String id, String name, String type, BigDecimal annualFeePercent) {
}
