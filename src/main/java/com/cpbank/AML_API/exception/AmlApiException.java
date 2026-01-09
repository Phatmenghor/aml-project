package com.cpbank.AML_API.exception;

public class AmlApiException extends RuntimeException {
    public AmlApiException(String message) {
        super(message);
    }
    
    public AmlApiException(String message, Throwable cause) {
        super(message, cause);
    }
}






