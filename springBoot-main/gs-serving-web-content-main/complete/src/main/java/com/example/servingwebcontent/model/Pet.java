package com.example.servingwebcontent.model;

import com.example.servingwebcontent.constants.DatabaseConstants;
import com.example.servingwebcontent.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Pet entity following OOP principles
 * Extends BaseEntity for common functionality (Inheritance)
 * Implements proper Encapsulation with validation
 */
public class Pet extends BaseEntity {
    
    // Private fields (Encapsulation)
    private Long petId;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private Double weight;
    private String color;
    private Gender gender;
    private Long ownerId;
    private HealthStatus healthStatus;
    
    // Additional fields for display purposes
    private String ownerName;
    private String ownerPhone;
    
    // Enums for type safety and better design
    public enum Gender {
        MALE("Male"),
        FEMALE("Female"),
        UNKNOWN("Unknown");
        
        private final String displayName;
        
        Gender(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static Gender fromString(String value) {
            if (value == null) return UNKNOWN;
            
            for (Gender gender : Gender.values()) {
                if (gender.displayName.equalsIgnoreCase(value) || 
                    gender.name().equalsIgnoreCase(value)) {
                    return gender;
                }
            }
            return UNKNOWN;
        }
    }
    
    public enum HealthStatus {
        HEALTHY("Healthy"),
        NEEDS_CHECKUP("Needs Checkup"),
        SICK("Sick"),
        RECOVERING("Recovering"),
        OVERWEIGHT("Overweight"),
        UNDERWEIGHT("Underweight");
        
        private final String displayName;
        
        HealthStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static HealthStatus fromString(String value) {
            if (value == null) return HEALTHY;
            
            for (HealthStatus status : HealthStatus.values()) {
                if (status.displayName.equalsIgnoreCase(value) || 
                    status.name().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            return HEALTHY;
        }
    }
    
    // Constructors
    public Pet() {
        super();
        this.gender = Gender.UNKNOWN;
        this.healthStatus = HealthStatus.HEALTHY;
    }
    
    public Pet(String name, String species, String breed, Integer age, Long ownerId) {
        this();
        this.setName(name);
        this.setSpecies(species);
        this.setBreed(breed);
        this.setAge(age);
        this.setOwnerId(ownerId);
    }
    
    // Override abstract method from BaseEntity
    @Override
    public Long getId() {
        return this.petId;
    }
    
    // Encapsulation: Getters with validation
    public Long getPetId() {
        return petId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSpecies() {
        return species;
    }
    
    public String getBreed() {
        return breed;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public String getColor() {
        return color;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public String getGenderDisplayName() {
        return gender != null ? gender.getDisplayName() : Gender.UNKNOWN.getDisplayName();
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public HealthStatus getHealthStatus() {
        return healthStatus;
    }
    
    public String getHealthStatusDisplayName() {
        return healthStatus != null ? healthStatus.getDisplayName() : HealthStatus.HEALTHY.getDisplayName();
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public String getOwnerPhone() {
        return ownerPhone;
    }
    
    // Encapsulation: Setters with validation
    public void setPetId(Long petId) {
        this.petId = petId;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Pet name cannot be null or empty");
        }
        if (name.length() > DatabaseConstants.MAX_NAME_LENGTH) {
            throw new ValidationException("Pet name cannot exceed " + DatabaseConstants.MAX_NAME_LENGTH + " characters");
        }
        this.name = name.trim();
        this.updateTimestamp();
    }
    
    public void setSpecies(String species) {
        if (species == null || species.trim().isEmpty()) {
            throw new ValidationException("Pet species cannot be null or empty");
        }
        this.species = species.trim();
        this.updateTimestamp();
    }
    
    public void setBreed(String breed) {
        this.breed = breed != null ? breed.trim() : null;
        this.updateTimestamp();
    }
    
    public void setAge(Integer age) {
        if (age != null && (age < DatabaseConstants.MIN_AGE || age > DatabaseConstants.MAX_AGE)) {
            throw new ValidationException("Pet age must be between " + DatabaseConstants.MIN_AGE + " and " + DatabaseConstants.MAX_AGE);
        }
        this.age = age;
        this.updateTimestamp();
    }
    
    public void setWeight(Double weight) {
        if (weight != null && (weight < DatabaseConstants.MIN_WEIGHT || weight > DatabaseConstants.MAX_WEIGHT)) {
            throw new ValidationException("Pet weight must be between " + DatabaseConstants.MIN_WEIGHT + " and " + DatabaseConstants.MAX_WEIGHT + " kg");
        }
        this.weight = weight;
        this.updateTimestamp();
    }
    
    public void setColor(String color) {
        this.color = color != null ? color.trim() : null;
        this.updateTimestamp();
    }
    
    public void setGender(Gender gender) {
        this.gender = gender != null ? gender : Gender.UNKNOWN;
        this.updateTimestamp();
    }
    
    public void setGender(String genderString) {
        this.setGender(Gender.fromString(genderString));
    }
    
    public void setOwnerId(Long ownerId) {
        if (ownerId != null && ownerId <= 0) {
            throw new ValidationException("Owner ID must be positive");
        }
        this.ownerId = ownerId;
        this.updateTimestamp();
    }
    
    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus != null ? healthStatus : HealthStatus.HEALTHY;
        this.updateTimestamp();
    }
    
    public void setHealthStatus(String healthStatusString) {
        this.setHealthStatus(HealthStatus.fromString(healthStatusString));
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }
    
    // Override validation from BaseEntity (Polymorphism)
    @Override
    public boolean isValid() {
        // For new pets, we don't require an ID yet (it will be set when saved)
        // Only check ID if it exists
        boolean idValid = petId == null || petId > 0;
        boolean nameValid = name != null && !name.trim().isEmpty();
        boolean speciesValid = species != null && !species.trim().isEmpty();

        return idValid && nameValid && speciesValid;
    }
    
    // Business logic methods
    public boolean isAdult() {
        if (age == null) return false;
        
        // Different maturity ages for different species
        return switch (species.toLowerCase()) {
            case "dog" -> age >= 1;
            case "cat" -> age >= 1;
            case "rabbit" -> age >= 1;
            default -> age >= 2;
        };
    }
    
    public boolean needsVaccination() {
        return age != null && age >= 1 && 
               (healthStatus == HealthStatus.HEALTHY || healthStatus == HealthStatus.NEEDS_CHECKUP);
    }
    
    public String getDisplayInfo() {
        StringBuilder info = new StringBuilder();
        info.append(name);
        if (breed != null && !breed.isEmpty()) {
            info.append(" (").append(breed).append(")");
        }
        if (age != null) {
            info.append(", ").append(age).append(" years old");
        }
        return info.toString();
    }
    
    // Override equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Pet pet = (Pet) obj;
        return Objects.equals(petId, pet.petId) &&
               Objects.equals(name, pet.name) &&
               Objects.equals(species, pet.species) &&
               Objects.equals(ownerId, pet.ownerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(petId, name, species, ownerId);
    }
    
    @Override
    public String toString() {
        return String.format("Pet{id=%d, name='%s', species='%s', breed='%s', age=%d, owner=%d}", 
                           petId, name, species, breed, age, ownerId);
    }
}
