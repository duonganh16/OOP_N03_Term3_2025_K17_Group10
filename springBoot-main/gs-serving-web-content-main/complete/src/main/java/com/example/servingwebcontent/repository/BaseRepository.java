package com.example.servingwebcontent.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic Repository Interface following SOLID principles
 * Provides abstraction for CRUD operations
 * 
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface BaseRepository<T, ID> {
    
    // CREATE
    T save(T entity);
    
    // READ
    List<T> findAll();
    Optional<T> findById(ID id);
    
    // UPDATE
    T update(T entity);
    
    // DELETE
    boolean deleteById(ID id);
    
    // UTILITY
    long count();
    boolean existsById(ID id);
    
    // SEARCH
    List<T> findByName(String name);
}
