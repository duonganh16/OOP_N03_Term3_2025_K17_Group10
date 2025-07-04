package com.example.servingwebcontent.util;

import com.example.servingwebcontent.exception.ValidationException;

/**
 * Utility class for search parameter validation
 * Follows Single Responsibility Principle - handles only search validation
 * Implements DRY principle - reusable across all controllers
 */
public class SearchValidationUtil {
    
    // Constants for validation limits
    private static final int MAX_SEARCH_TERM_LENGTH = 100;
    private static final int MIN_SEARCH_TERM_LENGTH = 1;
    private static final String SEARCH_TERM_PATTERN = "^[a-zA-Z0-9\\s\\-\\.@_]+$";
    
    /**
     * Validates and sanitizes a search query string
     * @param query The search query to validate
     * @return Sanitized query string or null if invalid
     * @throws ValidationException if query contains invalid characters
     */
    public static String validateAndSanitizeQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }
        
        String trimmedQuery = query.trim();
        
        // Check length constraints
        if (trimmedQuery.length() < MIN_SEARCH_TERM_LENGTH) {
            return null;
        }
        
        if (trimmedQuery.length() > MAX_SEARCH_TERM_LENGTH) {
            throw new ValidationException("Search term too long. Maximum " + MAX_SEARCH_TERM_LENGTH + " characters allowed.");
        }
        
        // Check for valid characters (prevent injection attacks)
        if (!trimmedQuery.matches(SEARCH_TERM_PATTERN)) {
            throw new ValidationException("Search term contains invalid characters. Only letters, numbers, spaces, hyphens, dots, @ and underscores are allowed.");
        }
        
        return trimmedQuery;
    }
    
    /**
     * Validates an email search parameter
     * @param email The email to validate
     * @return Sanitized email or null if invalid
     * @throws ValidationException if email format is invalid
     */
    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        String trimmedEmail = email.trim().toLowerCase();
        
        // Basic email validation pattern
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        
        if (!trimmedEmail.matches(emailPattern)) {
            throw new ValidationException("Invalid email format.");
        }
        
        if (trimmedEmail.length() > MAX_SEARCH_TERM_LENGTH) {
            throw new ValidationException("Email too long. Maximum " + MAX_SEARCH_TERM_LENGTH + " characters allowed.");
        }
        
        return trimmedEmail;
    }
    
    /**
     * Validates a species filter parameter
     * @param species The species to validate
     * @return Sanitized species or null if invalid
     */
    public static String validateSpecies(String species) {
        if (species == null || species.trim().isEmpty()) {
            return null;
        }
        
        String trimmedSpecies = species.trim();
        
        // Only allow alphabetic characters and spaces for species
        if (!trimmedSpecies.matches("^[a-zA-Z\\s]+$")) {
            throw new ValidationException("Species name can only contain letters and spaces.");
        }
        
        if (trimmedSpecies.length() > 50) {
            throw new ValidationException("Species name too long. Maximum 50 characters allowed.");
        }
        
        return trimmedSpecies;
    }
    
    /**
     * Validates a pet ID parameter
     * @param petId The pet ID to validate
     * @return Valid pet ID or null if invalid
     * @throws ValidationException if pet ID is invalid
     */
    public static Long validatePetId(Long petId) {
        if (petId == null) {
            return null;
        }
        
        if (petId <= 0) {
            throw new ValidationException("Pet ID must be a positive number.");
        }
        
        if (petId > Long.MAX_VALUE / 2) { // Reasonable upper bound
            throw new ValidationException("Pet ID is too large.");
        }
        
        return petId;
    }
    
    /**
     * Sanitizes a string by removing potentially harmful characters
     * @param input The input string to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove HTML tags and script content
        String sanitized = input.replaceAll("<[^>]*>", "")
                               .replaceAll("javascript:", "")
                               .replaceAll("vbscript:", "")
                               .replaceAll("onload", "")
                               .replaceAll("onerror", "")
                               .trim();
        
        return sanitized.isEmpty() ? null : sanitized;
    }
    
    /**
     * Checks if a search term is potentially dangerous
     * @param searchTerm The search term to check
     * @return true if the term appears safe, false otherwise
     */
    public static boolean isSafeSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return true;
        }
        
        String lowerTerm = searchTerm.toLowerCase();
        
        // Check for SQL injection patterns
        String[] dangerousPatterns = {
            "select", "insert", "update", "delete", "drop", "create", "alter",
            "union", "script", "javascript", "vbscript", "onload", "onerror",
            "--", "/*", "*/", "xp_", "sp_"
        };
        
        for (String pattern : dangerousPatterns) {
            if (lowerTerm.contains(pattern)) {
                return false;
            }
        }
        
        return true;
    }
}
