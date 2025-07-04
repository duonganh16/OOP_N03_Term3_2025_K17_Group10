package com.example.servingwebcontent.exception;

/**
 * Database Exception for Pet Care system
 * Thrown when database operations fail
 */
public class DatabaseException extends PetCareException {
    
    public DatabaseException(String message, Throwable cause) {
        super(message, "DATABASE_ERROR", cause);
    }
    
    public DatabaseException(String message) {
        super(message, "DATABASE_ERROR");
    }
}
