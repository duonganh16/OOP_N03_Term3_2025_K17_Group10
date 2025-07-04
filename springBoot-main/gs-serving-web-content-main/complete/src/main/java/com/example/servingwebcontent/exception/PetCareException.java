package com.example.servingwebcontent.exception;

/**
 * Base exception class for Pet Care system
 * Follows OOP inheritance principles
 */
public class PetCareException extends RuntimeException {
    
    private final String errorCode;
    
    public PetCareException(String message) {
        super(message);
        this.errorCode = "GENERAL_ERROR";
    }
    
    public PetCareException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public PetCareException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GENERAL_ERROR";
    }
    
    public PetCareException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
