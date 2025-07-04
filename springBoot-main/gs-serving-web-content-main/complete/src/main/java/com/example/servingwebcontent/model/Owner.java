package com.example.servingwebcontent.model;

import com.example.servingwebcontent.constants.DatabaseConstants;
import com.example.servingwebcontent.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Owner entity following OOP principles
 * Extends BaseEntity for common functionality (Inheritance)
 * Implements proper Encapsulation with validation
 */
public class Owner extends BaseEntity {
    
    // Private fields (Encapsulation)
    private Long ownerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    
    // Composition: Owner has many pets
    private List<Pet> pets;
    
    // Constants for validation
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9+\\-\\s()]{10,20}$");
    
    // Constructors
    public Owner() {
        super();
        this.pets = new ArrayList<>();
    }
    
    public Owner(String name, String email, String phone, String address) {
        this();
        this.setName(name);
        this.setEmail(email);
        this.setPhone(phone);
        this.setAddress(address);
    }
    
    // Override abstract method from BaseEntity
    @Override
    public Long getId() {
        return this.ownerId;
    }
    
    // Encapsulation: Getters
    public Long getOwnerId() {
        return ownerId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public List<Pet> getPets() {
        return new ArrayList<>(pets); // Return defensive copy
    }
    
    // Encapsulation: Setters with validation
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Owner name cannot be null or empty");
        }
        if (name.length() > DatabaseConstants.MAX_NAME_LENGTH) {
            throw new ValidationException("Owner name cannot exceed " + DatabaseConstants.MAX_NAME_LENGTH + " characters");
        }
        this.name = name.trim();
        this.updateTimestamp();
    }
    
    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (email.length() > DatabaseConstants.MAX_EMAIL_LENGTH) {
                throw new ValidationException("Email cannot exceed " + DatabaseConstants.MAX_EMAIL_LENGTH + " characters");
            }
            if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                throw new ValidationException("Invalid email format");
            }
        }
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.updateTimestamp();
    }
    
    public void setPhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            if (phone.length() > DatabaseConstants.MAX_PHONE_LENGTH) {
                throw new ValidationException("Phone cannot exceed " + DatabaseConstants.MAX_PHONE_LENGTH + " characters");
            }
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new ValidationException("Invalid phone format");
            }
        }
        this.phone = phone != null ? phone.trim() : null;
        this.updateTimestamp();
    }
    
    public void setAddress(String address) {
        this.address = address != null ? address.trim() : null;
        this.updateTimestamp();
    }
    
    public void setPets(List<Pet> pets) {
        this.pets = pets != null ? new ArrayList<>(pets) : new ArrayList<>();
    }
    
    // Business logic methods
    public void addPet(Pet pet) {
        if (pet == null) {
            throw new ValidationException("Cannot add null pet");
        }
        if (!pets.contains(pet)) {
            pets.add(pet);
            pet.setOwnerId(this.ownerId);
        }
    }
    
    public void removePet(Pet pet) {
        if (pet != null) {
            pets.remove(pet);
            pet.setOwnerId(null);
        }
    }
    
    public int getPetCount() {
        return pets.size();
    }
    
    public List<Pet> getPetsBySpecies(String species) {
        return pets.stream()
                   .filter(pet -> species.equalsIgnoreCase(pet.getSpecies()))
                   .collect(java.util.stream.Collectors.toList());
    }
    
    public boolean hasPets() {
        return !pets.isEmpty();
    }
    
    public boolean hasHealthyPets() {
        return pets.stream()
                   .anyMatch(pet -> pet.getHealthStatus() == Pet.HealthStatus.HEALTHY);
    }
    
    public boolean hasSickPets() {
        return pets.stream()
                   .anyMatch(pet -> pet.getHealthStatus() == Pet.HealthStatus.SICK);
    }
    
    public String getContactInfo() {
        StringBuilder contact = new StringBuilder();
        if (phone != null && !phone.isEmpty()) {
            contact.append("Phone: ").append(phone);
        }
        if (email != null && !email.isEmpty()) {
            if (contact.length() > 0) contact.append(", ");
            contact.append("Email: ").append(email);
        }
        return contact.toString();
    }
    
    public String getDisplayName() {
        return name != null ? name : "Unknown Owner";
    }
    
    // Override validation from BaseEntity (Polymorphism)
    @Override
    public boolean isValid() {
        // For new owners, we don't require an ID yet (it will be set when saved)
        // Only check ID if it exists
        boolean idValid = ownerId == null || ownerId > 0;
        boolean nameValid = name != null && !name.trim().isEmpty();
        boolean emailValid = email == null || EMAIL_PATTERN.matcher(email).matches();
        boolean phoneValid = phone == null || PHONE_PATTERN.matcher(phone).matches();

        return idValid && nameValid && emailValid && phoneValid;
    }
    
    // Utility methods
    public boolean hasValidContactInfo() {
        return (email != null && !email.isEmpty()) || (phone != null && !phone.isEmpty());
    }
    
    public String getShortAddress() {
        if (address == null || address.isEmpty()) {
            return "No address provided";
        }
        return address.length() > 50 ? address.substring(0, 47) + "..." : address;
    }
    
    // Override equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Owner owner = (Owner) obj;
        return Objects.equals(ownerId, owner.ownerId) &&
               Objects.equals(name, owner.name) &&
               Objects.equals(email, owner.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ownerId, name, email);
    }
    
    @Override
    public String toString() {
        return String.format("Owner{id=%d, name='%s', email='%s', phone='%s', pets=%d}", 
                           ownerId, name, email, phone, pets.size());
    }
}
