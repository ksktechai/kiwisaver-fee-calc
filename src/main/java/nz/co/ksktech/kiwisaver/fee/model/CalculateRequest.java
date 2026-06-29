package nz.co.ksktech.kiwisaver.fee.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record CalculateRequest(String fundId, double balance, double annualContribution,
                                int years, double annualReturnPercent) {
}
