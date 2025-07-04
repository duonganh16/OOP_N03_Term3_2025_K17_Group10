package com.example.servingwebcontent.service;

import com.example.servingwebcontent.model.Owner;
import com.example.servingwebcontent.model.Pet;

import java.util.List;
import java.util.Optional;

/**
 * Owner Service Interface following SOLID principles
 * Defines business logic contract for Owner operations
 */
public interface OwnerService extends BaseService<Owner, Long> {
    
    // Owner-specific business operations
    Optional<Owner> getOwnerByEmail(String email);
    List<Owner> getOwnersByPhone(String phone);
    List<Owner> getOwnersWithPets();
    List<Owner> getOwnersWithoutPets();
    
    // Business logic operations
    boolean registerNewOwner(Owner owner);
    boolean updateOwnerContact(Long ownerId, String email, String phone);
    boolean transferAllPetsToNewOwner(Long fromOwnerId, Long toOwnerId);
    
    // Pet-related operations
    List<Pet> getOwnerPets(Long ownerId);
    boolean addPetToOwner(Long ownerId, Pet pet);
    boolean removePetFromOwner(Long ownerId, Long petId);
    
    // Statistics and reporting
    long getTotalOwnersCount();
    long getOwnersWithPetsCount();
    double getAveragePetsPerOwner();
    
    // Validation and business rules
    boolean isEmailAvailable(String email);
    boolean canOwnerAdoptMorePets(Long ownerId);
    boolean hasValidContactInfo(Long ownerId);
}
