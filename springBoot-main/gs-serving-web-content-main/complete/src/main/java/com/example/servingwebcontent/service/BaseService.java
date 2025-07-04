package com.example.servingwebcontent.service;

import java.util.List;
import java.util.Optional;

/**
 * Generic Service Interface following SOLID principles
 * Defines business logic contract
 * 
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface BaseService<T, ID> {
    
    // Business operations
    T create(T entity);
    List<T> getAllEntities();
    Optional<T> getEntityById(ID id);
    T updateEntity(T entity);
    boolean deleteEntity(ID id);
    
    // Business logic
    long getTotalCount();
    boolean entityExists(ID id);
    List<T> searchEntities(String searchTerm);
    
    // Validation
    boolean isValidEntity(T entity);
}
