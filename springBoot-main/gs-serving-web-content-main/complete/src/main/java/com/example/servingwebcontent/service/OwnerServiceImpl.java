package com.example.servingwebcontent.service;

import com.example.servingwebcontent.exception.EntityNotFoundException;
import com.example.servingwebcontent.exception.ValidationException;
import com.example.servingwebcontent.model.Owner;
import com.example.servingwebcontent.model.Pet;
import com.example.servingwebcontent.repository.OwnerRepositoryImpl;
import com.example.servingwebcontent.repository.PetRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Owner Service Implementation following OOP principles
 * Implements OwnerService interface (Abstraction)
 * Uses Repository for data access (Dependency Injection)
 */
@Service
public class OwnerServiceImpl implements OwnerService {
    
    private final OwnerRepositoryImpl ownerRepository;
    private final PetRepositoryImpl petRepository;
    
    @Autowired
    public OwnerServiceImpl(OwnerRepositoryImpl ownerRepository, PetRepositoryImpl petRepository) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
    }
    
    // Implement BaseService interface methods
    
    @Override
    public Owner create(Owner owner) {
        // Use detailed validation for better error messages
        validateOwnerWithDetails(owner);

        // Business logic: Check if email is already taken
        if (owner.getEmail() != null && !isEmailAvailable(owner.getEmail())) {
            throw new ValidationException("Email is already registered");
        }

        return ownerRepository.save(owner);
    }
    
    @Override
    public List<Owner> getAllEntities() {
        return ownerRepository.findAll();
    }
    
    @Override
    public Optional<Owner> getEntityById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return ownerRepository.findById(id);
    }
    
    @Override
    public Owner updateEntity(Owner owner) {
        if (owner == null || owner.getOwnerId() == null) {
            throw new ValidationException("Owner and Owner ID cannot be null for update");
        }
        
        if (!ownerRepository.existsById(owner.getOwnerId())) {
            throw new EntityNotFoundException("Owner", owner.getOwnerId());
        }
        
        if (!isValidEntity(owner)) {
            throw new ValidationException("Invalid owner data for update");
        }
        
        // Business logic: Check email uniqueness (excluding current owner)
        if (owner.getEmail() != null) {
            Optional<Owner> existingOwner = ownerRepository.findByEmail(owner.getEmail());
            if (existingOwner.isPresent() && !existingOwner.get().getOwnerId().equals(owner.getOwnerId())) {
                throw new ValidationException("Email is already registered by another owner");
            }
        }
        
        return ownerRepository.update(owner);
    }
    
    @Override
    public boolean deleteEntity(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        if (!ownerRepository.existsById(id)) {
            throw new EntityNotFoundException("Owner", id);
        }
        
        // Business logic: Check if owner has pets
        List<Pet> ownerPets = getOwnerPets(id);
        if (!ownerPets.isEmpty()) {
            throw new ValidationException("Cannot delete owner with pets. Please transfer pets first.");
        }
        
        return ownerRepository.deleteById(id);
    }
    
    @Override
    public long getTotalCount() {
        return ownerRepository.count();
    }
    
    @Override
    public boolean entityExists(Long id) {
        return ownerRepository.existsById(id);
    }
    
    @Override
    public List<Owner> searchEntities(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllEntities();
        }
        return ownerRepository.findByName(searchTerm.trim());
    }
    
    @Override
    public boolean isValidEntity(Owner owner) {
        if (owner == null) {
            return false;
        }

        // Use the entity's own validation method (Polymorphism)
        if (!owner.isValid()) {
            return false;
        }

        // Additional business validation
        if (owner.getName() == null || owner.getName().trim().isEmpty()) {
            return false;
        }

        // At least one contact method should be provided
        if (!owner.hasValidContactInfo()) {
            return false;
        }

        return true;
    }

    /**
     * Validates owner and provides specific error messages
     * @param owner The owner to validate
     * @throws ValidationException with specific error message
     */
    private void validateOwnerWithDetails(Owner owner) {
        if (owner == null) {
            throw new ValidationException("Owner cannot be null");
        }

        // Check name
        if (owner.getName() == null || owner.getName().trim().isEmpty()) {
            throw new ValidationException("Owner name is required");
        }

        // Check contact info
        if (!owner.hasValidContactInfo()) {
            throw new ValidationException("At least one contact method (email or phone) is required");
        }

        // Check email format if provided
        if (owner.getEmail() != null && !owner.getEmail().trim().isEmpty()) {
            try {
                owner.setEmail(owner.getEmail()); // This will validate the format
            } catch (ValidationException e) {
                throw new ValidationException("Invalid email format: " + e.getMessage());
            }
        }

        // Check phone format if provided
        if (owner.getPhone() != null && !owner.getPhone().trim().isEmpty()) {
            try {
                owner.setPhone(owner.getPhone()); // This will validate the format
            } catch (ValidationException e) {
                throw new ValidationException("Invalid phone format: " + e.getMessage());
            }
        }
    }
    
    // Implement OwnerService specific methods
    
    @Override
    public Optional<Owner> getOwnerByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return ownerRepository.findByEmail(email.trim());
    }
    
    @Override
    public List<Owner> getOwnersByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return List.of();
        }
        return ownerRepository.findByPhone(phone.trim());
    }
    
    @Override
    public List<Owner> getOwnersWithPets() {
        return getAllEntities().stream()
                .filter(owner -> !getOwnerPets(owner.getOwnerId()).isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Owner> getOwnersWithoutPets() {
        return getAllEntities().stream()
                .filter(owner -> getOwnerPets(owner.getOwnerId()).isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public boolean registerNewOwner(Owner owner) {
        try {
            create(owner);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean updateOwnerContact(Long ownerId, String email, String phone) {
        Optional<Owner> ownerOpt = getEntityById(ownerId);
        if (ownerOpt.isEmpty()) {
            return false;
        }
        
        Owner owner = ownerOpt.get();
        owner.setEmail(email);
        owner.setPhone(phone);
        
        try {
            updateEntity(owner);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean transferAllPetsToNewOwner(Long fromOwnerId, Long toOwnerId) {
        if (fromOwnerId == null || toOwnerId == null || fromOwnerId.equals(toOwnerId)) {
            return false;
        }
        
        if (!entityExists(fromOwnerId) || !entityExists(toOwnerId)) {
            return false;
        }
        
        List<Pet> pets = getOwnerPets(fromOwnerId);
        
        try {
            for (Pet pet : pets) {
                pet.setOwnerId(toOwnerId);
                petRepository.update(pet);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public List<Pet> getOwnerPets(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            return List.of();
        }
        return petRepository.findByOwnerId(ownerId);
    }
    
    @Override
    public boolean addPetToOwner(Long ownerId, Pet pet) {
        if (ownerId == null || pet == null) {
            return false;
        }
        
        if (!entityExists(ownerId)) {
            return false;
        }
        
        pet.setOwnerId(ownerId);
        
        try {
            petRepository.save(pet);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean removePetFromOwner(Long ownerId, Long petId) {
        if (ownerId == null || petId == null) {
            return false;
        }
        
        Optional<Pet> petOpt = petRepository.findById(petId);
        if (petOpt.isEmpty() || !ownerId.equals(petOpt.get().getOwnerId())) {
            return false;
        }
        
        return petRepository.deleteById(petId);
    }
    
    @Override
    public long getTotalOwnersCount() {
        return getTotalCount();
    }
    
    @Override
    public long getOwnersWithPetsCount() {
        return getOwnersWithPets().size();
    }
    
    @Override
    public double getAveragePetsPerOwner() {
        long totalOwners = getTotalOwnersCount();
        if (totalOwners == 0) {
            return 0.0;
        }
        
        long totalPets = petRepository.count();
        return (double) totalPets / totalOwners;
    }
    
    @Override
    public boolean isEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        return getOwnerByEmail(email.trim()).isEmpty();
    }
    
    @Override
    public boolean canOwnerAdoptMorePets(Long ownerId) {
        if (ownerId == null) {
            return false;
        }
        
        // Business rule: Maximum 5 pets per owner
        List<Pet> ownerPets = getOwnerPets(ownerId);
        return ownerPets.size() < 5;
    }
    
    @Override
    public boolean hasValidContactInfo(Long ownerId) {
        Optional<Owner> ownerOpt = getEntityById(ownerId);
        return ownerOpt.map(Owner::hasValidContactInfo).orElse(false);
    }
}
