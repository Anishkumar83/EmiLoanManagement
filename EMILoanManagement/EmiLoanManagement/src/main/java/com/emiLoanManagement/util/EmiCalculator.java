package com.emiLoanManagement.util;

public class EmiCalculator {

    public static double calculateEmi(double principal, double annualRate, double tenureMonth){
        double monthlyRate = annualRate/ (12 * 100);

        return principal * monthlyRate * Math.pow(1+monthlyRate,tenureMonth)
                / (Math.pow(1+monthlyRate,tenureMonth)-1);
    }
}
