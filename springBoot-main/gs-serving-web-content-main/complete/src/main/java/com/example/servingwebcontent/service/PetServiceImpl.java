package com.example.servingwebcontent.service;

import com.example.servingwebcontent.exception.EntityNotFoundException;
import com.example.servingwebcontent.exception.ValidationException;
import com.example.servingwebcontent.model.Pet;
import com.example.servingwebcontent.repository.PetRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Pet Service Implementation following OOP principles
 * Implements PetService interface (Abstraction)
 * Uses Repository for data access (Dependency Injection)
 * Handles business logic and validation (Single Responsibility)
 */
@Service
public class PetServiceImpl implements PetService {
    
    private final PetRepositoryImpl petRepository;
    
    // Constructor injection (Dependency Injection)
    @Autowired
    public PetServiceImpl(PetRepositoryImpl petRepository) {
        this.petRepository = petRepository;
    }
    
    // Implement BaseService interface methods
    
    @Override
    public Pet create(Pet pet) {
        if (pet == null) {
            throw new ValidationException("Pet cannot be null");
        }
        
        if (!isValidEntity(pet)) {
            throw new ValidationException("Invalid pet data");
        }
        
        // Business logic: Set default values if needed
        if (pet.getHealthStatus() == null) {
            pet.setHealthStatus(Pet.HealthStatus.HEALTHY);
        }
        
        if (pet.getGender() == null) {
            pet.setGender(Pet.Gender.UNKNOWN);
        }
        
        return petRepository.save(pet);
    }
    
    @Override
    public List<Pet> getAllEntities() {
        return petRepository.findAll();
    }
    
    @Override
    public Optional<Pet> getEntityById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return petRepository.findById(id);
    }
    
    @Override
    public Pet updateEntity(Pet pet) {
        if (pet == null || pet.getPetId() == null) {
            throw new ValidationException("Pet and Pet ID cannot be null for update");
        }
        
        if (!petRepository.existsById(pet.getPetId())) {
            throw new EntityNotFoundException("Pet", pet.getPetId());
        }
        
        if (!isValidEntity(pet)) {
            throw new ValidationException("Invalid pet data for update");
        }
        
        return petRepository.update(pet);
    }
    
    @Override
    public boolean deleteEntity(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        if (!petRepository.existsById(id)) {
            throw new EntityNotFoundException("Pet", id);
        }
        
        // Business logic: Check if pet can be deleted
        // For example, check if pet has upcoming appointments
        
        return petRepository.deleteById(id);
    }
    
    @Override
    public long getTotalCount() {
        return petRepository.count();
    }
    
    @Override
    public boolean entityExists(Long id) {
        return petRepository.existsById(id);
    }
    
    @Override
    public List<Pet> searchEntities(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllEntities();
        }
        return petRepository.findByName(searchTerm.trim());
    }
    
    @Override
    public boolean isValidEntity(Pet pet) {
        if (pet == null) {
            return false;
        }

        // Basic validation - only check required fields
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            System.out.println("Validation failed: Pet name is null or empty");
            return false;
        }

        if (pet.getSpecies() == null || pet.getSpecies().trim().isEmpty()) {
            System.out.println("Validation failed: Pet species is null or empty");
            return false;
        }

        // Optional field validation - only if provided
        if (pet.getAge() != null && (pet.getAge() < 0 || pet.getAge() > 50)) {
            System.out.println("Validation failed: Pet age out of range: " + pet.getAge());
            return false;
        }

        if (pet.getWeight() != null && (pet.getWeight() <= 0 || pet.getWeight() > 200)) {
            System.out.println("Validation failed: Pet weight out of range: " + pet.getWeight());
            return false;
        }

        System.out.println("Pet validation passed for: " + pet.getName());
        return true;
    }
    
    // Implement PetService specific methods
    
    @Override
    public List<Pet> getPetsByOwner(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            return List.of();
        }
        return petRepository.findByOwnerId(ownerId);
    }
    
    @Override
    public List<Pet> getPetsBySpecies(String species) {
        if (species == null || species.trim().isEmpty()) {
            return List.of();
        }
        return petRepository.findBySpecies(species.trim());
    }
    
    @Override
    public List<Pet> getHealthyPets() {
        return getAllEntities().stream()
                .filter(pet -> pet.getHealthStatus() == Pet.HealthStatus.HEALTHY)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pet> getPetsNeedingCheckup() {
        return getAllEntities().stream()
                .filter(pet -> pet.getHealthStatus() == Pet.HealthStatus.NEEDS_CHECKUP ||
                              pet.getHealthStatus() == Pet.HealthStatus.SICK)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean transferPetToNewOwner(Long petId, Long newOwnerId) {
        if (petId == null || newOwnerId == null || petId <= 0 || newOwnerId <= 0) {
            return false;
        }
        
        Optional<Pet> petOpt = getEntityById(petId);
        if (petOpt.isEmpty()) {
            throw new EntityNotFoundException("Pet", petId);
        }
        
        Pet pet = petOpt.get();
        
        // Business logic: Check if transfer is allowed
        if (pet.getHealthStatus() == Pet.HealthStatus.SICK) {
            throw new ValidationException("Cannot transfer sick pet");
        }
        
        pet.setOwnerId(newOwnerId);
        updateEntity(pet);
        return true;
    }
    
    @Override
    public void updateHealthStatus(Long petId, Pet.HealthStatus newStatus) {
        if (petId == null || newStatus == null) {
            throw new ValidationException("Pet ID and health status cannot be null");
        }
        
        Optional<Pet> petOpt = getEntityById(petId);
        if (petOpt.isEmpty()) {
            throw new EntityNotFoundException("Pet", petId);
        }
        
        Pet pet = petOpt.get();
        pet.setHealthStatus(newStatus);
        updateEntity(pet);
    }
    
    @Override
    public Map<String, Long> getSpeciesStatistics() {
        return getAllEntities().stream()
                .collect(Collectors.groupingBy(
                    Pet::getSpecies,
                    Collectors.counting()
                ));
    }
    
    @Override
    public Map<String, Long> getHealthStatusStatistics() {
        return getAllEntities().stream()
                .collect(Collectors.groupingBy(
                    pet -> pet.getHealthStatus().getDisplayName(),
                    Collectors.counting()
                ));
    }
    
    @Override
    public long getCountByOwner(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            return 0;
        }
        return getPetsByOwner(ownerId).size();
    }
    
    @Override
    public boolean canPetBeAdopted(Long petId) {
        Optional<Pet> petOpt = getEntityById(petId);
        if (petOpt.isEmpty()) {
            return false;
        }
        
        Pet pet = petOpt.get();
        
        // Business rules for adoption
        return pet.getHealthStatus() == Pet.HealthStatus.HEALTHY &&
               pet.getAge() != null && pet.getAge() >= 1; // Must be at least 1 year old
    }
    
    @Override
    public boolean isPetEligibleForVaccination(Long petId) {
        Optional<Pet> petOpt = getEntityById(petId);
        if (petOpt.isEmpty()) {
            return false;
        }
        
        Pet pet = petOpt.get();
        return pet.needsVaccination(); // Use Pet's business logic method
    }
    
    @Override
    public List<Pet> getPetsRequiringVaccination() {
        return getAllEntities().stream()
                .filter(Pet::needsVaccination)
                .collect(Collectors.toList());
    }
    
    // Additional utility methods
    public List<Pet> getAdultPets() {
        return getAllEntities().stream()
                .filter(Pet::isAdult)
                .collect(Collectors.toList());
    }
    
    public List<Pet> getPetsByAgeRange(int minAge, int maxAge) {
        return getAllEntities().stream()
                .filter(pet -> pet.getAge() != null && 
                              pet.getAge() >= minAge && 
                              pet.getAge() <= maxAge)
                .collect(Collectors.toList());
    }
    
    public double getAverageAge() {
        return getAllEntities().stream()
                .filter(pet -> pet.getAge() != null)
                .mapToInt(Pet::getAge)
                .average()
                .orElse(0.0);
    }
}
