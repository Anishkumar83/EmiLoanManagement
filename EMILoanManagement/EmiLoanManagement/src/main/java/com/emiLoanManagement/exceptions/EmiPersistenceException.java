package com.emiLoanManagement.exceptions;

public class EmiPersistenceException extends RuntimeException{
    public EmiPersistenceException(String message){
        super(message);
    }

    public EmiPersistenceException(String message, Throwable cause){
        super(message,cause);
    }
}
