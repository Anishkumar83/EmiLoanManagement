package com.emiLoanManagement.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class EmiCalculator {

    private static final int SCALE = 2;


    public static BigDecimal calculateEmi(BigDecimal principal,
                                          BigDecimal annualRate,
                                          int tenureMonths) {

        if (principal.compareTo(BigDecimal.ZERO) <= 0 ||
                annualRate.compareTo(BigDecimal.ZERO) < 0 ||
                tenureMonths <= 0) {
            throw new IllegalArgumentException("Invalid EMI parameters");
        }

        // monthlyRate = annualRate / (12 * 100)
        BigDecimal monthlyRate =
                annualRate.divide(BigDecimal.valueOf(1200),
                        10, RoundingMode.HALF_UP);

        // Case: Zero interest loan
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal
                    .divide(BigDecimal.valueOf(tenureMonths),
                            SCALE, RoundingMode.HALF_UP);
        }

        // (1 + r)^n
        BigDecimal onePlusRPowerN =
                BigDecimal.ONE.add(monthlyRate)
                        .pow(tenureMonths, MathContext.DECIMAL64);

        // EMI formula
        BigDecimal emi = principal
                .multiply(monthlyRate)
                .multiply(onePlusRPowerN)
                .divide(
                        onePlusRPowerN.subtract(BigDecimal.ONE),
                        SCALE,
                        RoundingMode.HALF_UP
                );

        if (emi.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("EMI calculation failed");
        }

        return emi;
    }
}
