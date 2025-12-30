package com.emiloanmanagement.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmiCalculatorTest {

    @Test
    void shouldCalculateCorrectResult(){
        BigDecimal principal=new BigDecimal("10000");
        BigDecimal rate = new BigDecimal("8");
        int months=12;

        BigDecimal emi=EmiCalculator.calculateEmi(principal,rate,months);

        assertNotNull(emi);
        assertEquals(0,emi.compareTo(new BigDecimal("869.88")));
    }

    @Test
    void zeroInterestLoanShouldSplitPrincipal(){
        BigDecimal principal = new BigDecimal("12000");
        BigDecimal rate = BigDecimal.ZERO;
        int month = 12;

        BigDecimal emi= EmiCalculator.calculateEmi(principal,rate,month);

        assertEquals(new BigDecimal("1000.00"),emi);
    }

    @Test
    void invalidInputsShouldThrowException(){

        assertThrows(IllegalArgumentException.class, ()->
                EmiCalculator.calculateEmi(BigDecimal.ZERO,
                        new BigDecimal("0"),12));
    }
}
