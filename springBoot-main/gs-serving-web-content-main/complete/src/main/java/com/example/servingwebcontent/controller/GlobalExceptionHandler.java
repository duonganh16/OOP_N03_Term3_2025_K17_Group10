package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.exception.EntityNotFoundException;
import com.example.servingwebcontent.exception.ValidationException;
import com.example.servingwebcontent.exception.DatabaseException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global Exception Handler for Pet Care System
 * Handles all exceptions across the application
 * Provides user-friendly error pages and messages
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(ValidationException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", "Validation Error: " + ex.getMessage());
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error";
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", "Not Found: " + ex.getMessage());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error";
    }
    
    @ExceptionHandler(DatabaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseException(DatabaseException ex, Model model, HttpServletRequest request) {
        model.addAttribute("error", "Database Error: " + ex.getMessage());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error";
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        // Log the full exception for debugging
        ex.printStackTrace();
        
        model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        // Add stack trace for development (remove in production)
        model.addAttribute("trace", ex.getStackTrace());
        
        return "error";
    }
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException ex, Model model, HttpServletRequest request) {
        // Log the full exception for debugging
        ex.printStackTrace();
        
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "An unexpected runtime error occurred";
        }
        
        model.addAttribute("error", errorMessage);
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        return "error";
    }
}
