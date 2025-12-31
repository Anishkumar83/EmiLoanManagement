package com.emiloanmanagement.exceptions;

public class NothingFoundException extends RuntimeException{
    public NothingFoundException(String message){
        super(message);
    }

    public NothingFoundException(String message, Throwable throwable){
        super(message,throwable);
    }
}
