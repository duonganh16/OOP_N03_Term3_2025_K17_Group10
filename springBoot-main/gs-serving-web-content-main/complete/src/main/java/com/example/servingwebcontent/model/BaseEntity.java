package com.example.servingwebcontent.model;

import java.time.LocalDateTime;

/**
 * Abstract base class for all entities
 * Implements common fields and behaviors using OOP principles
 */
public abstract class BaseEntity {
    
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    
    // Constructor
    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Encapsulation: Getters and Setters
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Abstract method - forces subclasses to implement
    public abstract Long getId();
    
    // Common behavior for all entities
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Polymorphism: Can be overridden by subclasses
    public boolean isValid() {
        return getId() != null && getId() > 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BaseEntity that = (BaseEntity) obj;
        return getId() != null && getId().equals(that.getId());
    }
    
    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
