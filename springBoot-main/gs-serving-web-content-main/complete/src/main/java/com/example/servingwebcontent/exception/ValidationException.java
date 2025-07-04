package com.example.servingwebcontent.exception;

/**
 * Validation Exception for Pet Care system
 * Thrown when validation rules are violated
 */
public class ValidationException extends PetCareException {
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", cause);
    }
}
