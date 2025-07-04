package com.example.servingwebcontent.service;

import com.example.servingwebcontent.model.Pet;

import java.util.List;
import java.util.Map;

/**
 * Pet Service Interface following SOLID principles
 * Defines business logic contract for Pet operations
 */
public interface PetService extends BaseService<Pet, Long> {

    // Pet-specific business operations
    List<Pet> getPetsByOwner(Long ownerId);
    List<Pet> getPetsBySpecies(String species);
    List<Pet> getHealthyPets();
    List<Pet> getPetsNeedingCheckup();

    // Business logic operations
    boolean transferPetToNewOwner(Long petId, Long newOwnerId);
    void updateHealthStatus(Long petId, Pet.HealthStatus newStatus);

    // Statistics and reporting
    Map<String, Long> getSpeciesStatistics();
    Map<String, Long> getHealthStatusStatistics();
    long getCountByOwner(Long ownerId);

    // Validation and business rules
    boolean canPetBeAdopted(Long petId);
    boolean isPetEligibleForVaccination(Long petId);
    List<Pet> getPetsRequiringVaccination();
}
