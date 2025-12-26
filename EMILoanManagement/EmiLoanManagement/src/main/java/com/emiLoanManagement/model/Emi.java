package com.emiLoanManagement.model;

import java.time.LocalDate;

public class Emi {

    private long emiId;
    private long loanId;
    private double emiAmount;
    private double interest_component;
    private double principal_component;
    private double outstanding_component;
    private LocalDate dueDate;
    private String status;


    public Emi() {
    }

    public double getInterest_component() {
        return interest_component;
    }

    public void setInterest_component(double interest_component) {
        this.interest_component = interest_component;
    }

    public double getPrincipal_component() {
        return principal_component;
    }

    public void setPrincipal_component(double principal_component) {
        this.principal_component = principal_component;
    }

    public double getOutstanding_component() {
        return outstanding_component;
    }

    public void setOutstanding_component(double outstanding_component) {
        this.outstanding_component = outstanding_component;
    }

    public Emi(long emiId, long loanId, double emiAmount, double interest_component,
               double principal_component, double outstanding_component, LocalDate dueDate, String status) {
        this.emiId = emiId;
        this.loanId = loanId;
        this.emiAmount = emiAmount;
        this.interest_component = interest_component;
        this.principal_component = principal_component;
        this.outstanding_component = outstanding_component;
        this.dueDate = dueDate;
        this.status = status;
    }

    public long getEmiId() {
        return emiId;
    }

    public void setEmiId(long emiId) {
        this.emiId = emiId;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public double getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
