package com.emiloanmanagement.model;

public class Loan {
    private long loanId;
    private String customerName;
    private double principal;
    private double interestRate;
    private int tenureMonths;

    public Loan() {
    }

    public Loan(long loanId, String customerName, double principal, double interestRate, int tenureMonths) {
        this.loanId = loanId;
        this.customerName = customerName;
        this.principal = principal;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(int tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}
