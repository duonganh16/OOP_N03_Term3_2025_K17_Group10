package com.example.servingwebcontent.exception;

/**
 * Entity Not Found Exception for Pet Care system
 * Thrown when requested entity is not found in database
 */
public class EntityNotFoundException extends PetCareException {
    
    public EntityNotFoundException(String entityType, Long id) {
        super(String.format("%s with ID %d not found", entityType, id), "ENTITY_NOT_FOUND");
    }
    
    public EntityNotFoundException(String message) {
        super(message, "ENTITY_NOT_FOUND");
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, "ENTITY_NOT_FOUND", cause);
    }
}
