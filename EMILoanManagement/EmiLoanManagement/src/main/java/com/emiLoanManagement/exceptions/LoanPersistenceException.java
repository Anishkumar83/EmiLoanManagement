package com.emiLoanManagement.exceptions;

public class LoanPersistenceException extends RuntimeException{

    public LoanPersistenceException(String message){
        super(message);
    }

    public LoanPersistenceException(String message, Throwable e){
        super(message,e);
    }

}
