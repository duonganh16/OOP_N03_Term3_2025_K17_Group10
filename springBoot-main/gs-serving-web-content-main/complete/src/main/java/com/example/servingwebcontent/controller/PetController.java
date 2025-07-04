package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.exception.EntityNotFoundException;
import com.example.servingwebcontent.exception.ValidationException;
import com.example.servingwebcontent.model.Pet;
import com.example.servingwebcontent.service.PetServiceImpl;
import com.example.servingwebcontent.service.OwnerServiceImpl;
import com.example.servingwebcontent.util.SearchValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Pet Controller following MVC pattern
 * Handles HTTP requests for Pet operations
 * Uses Service layer for business logic (Dependency Injection)
 */
@Controller
@RequestMapping("/pets")
public class PetController {
    
    private final PetServiceImpl petService;
    private final OwnerServiceImpl ownerService;
    
    @Autowired
    public PetController(PetServiceImpl petService, OwnerServiceImpl ownerService) {
        this.petService = petService;
        this.ownerService = ownerService;
    }
    
    // READ - Display all pets with search functionality
    @GetMapping
    public String listPets(@RequestParam(required = false) String query,
                          @RequestParam(required = false) String species,
                          Model model) {
        try {
            List<Pet> pets;
            String pageTitle = "Pet Management";

            // Handle search functionality with validation
            String validatedQuery = SearchValidationUtil.validateAndSanitizeQuery(query);
            String validatedSpecies = SearchValidationUtil.validateSpecies(species);

            if (validatedQuery != null) {
                pets = petService.searchEntities(validatedQuery);
                model.addAttribute("searchQuery", validatedQuery);
                pageTitle = "Search Results - " + pets.size() + " found";
            } else if (validatedSpecies != null) {
                pets = petService.getPetsBySpecies(validatedSpecies);
                model.addAttribute("searchSpecies", validatedSpecies);
                pageTitle = "Species Filter - " + pets.size() + " found";
            } else {
                pets = petService.getAllEntities();
            }

            // Calculate statistics
            Map<String, Long> speciesStats = petService.getSpeciesStatistics();
            Map<String, Long> healthStats = petService.getHealthStatusStatistics();

            model.addAttribute("pets", pets);
            model.addAttribute("speciesStats", speciesStats);
            model.addAttribute("healthStats", healthStats);
            model.addAttribute("totalPets", pets.size());
            model.addAttribute("pageTitle", pageTitle);

            // Add owners for potential future filtering
            model.addAttribute("owners", ownerService.getAllEntities());

            return "pets/list";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load pets: " + e.getMessage());
            return "error";
        }
    }
    
    // READ - Display single pet details
    @GetMapping("/{id}")
    public String viewPet(@PathVariable Long id, Model model) {
        try {
            Optional<Pet> petOpt = petService.getEntityById(id);
            if (petOpt.isEmpty()) {
                model.addAttribute("error", "Pet not found");
                return "error";
            }
            
            Pet pet = petOpt.get();
            model.addAttribute("pet", pet);
            model.addAttribute("pageTitle", "Pet Details - " + pet.getName());
            
            // Add owner information if available
            if (pet.getOwnerId() != null) {
                ownerService.getEntityById(pet.getOwnerId())
                           .ifPresent(owner -> model.addAttribute("owner", owner));
            }
            
            return "pets/view";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load pet: " + e.getMessage());
            return "error";
        }
    }
    
    // CREATE - Show form for new pet
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("pet", new Pet());
            model.addAttribute("owners", ownerService.getAllEntities());
            model.addAttribute("pageTitle", "Add New Pet");
            model.addAttribute("formAction", "/pets");
            model.addAttribute("submitText", "Add Pet");
            
            return "pets/form";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load form: " + e.getMessage());
            return "error";
        }
    }
    
    // CREATE - Process new pet form
    @PostMapping
    public String createPet(@ModelAttribute Pet pet, RedirectAttributes redirectAttributes, Model model) {
        try {
            // Debug logging
            System.out.println("Creating pet: " + pet.getName() + ", Species: " + pet.getSpecies());

            // Basic validation before service call
            if (pet.getName() == null || pet.getName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Pet name is required");
                return "redirect:/pets/new";
            }

            if (pet.getSpecies() == null || pet.getSpecies().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Pet species is required");
                return "redirect:/pets/new";
            }

            // Set default values for optional fields
            if (pet.getGender() == null) {
                pet.setGender(Pet.Gender.UNKNOWN);
            }

            if (pet.getHealthStatus() == null) {
                pet.setHealthStatus(Pet.HealthStatus.HEALTHY);
            }

            Pet savedPet = petService.create(pet);
            redirectAttributes.addFlashAttribute("success",
                "Pet '" + savedPet.getName() + "' has been added successfully!");
            return "redirect:/pets/" + savedPet.getPetId();
        } catch (ValidationException e) {
            System.out.println("Validation error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Validation error: " + e.getMessage());
            return "redirect:/pets/new";
        } catch (Exception e) {
            System.out.println("General error: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add pet: " + e.getMessage());
            return "redirect:/pets/new";
        }
    }
    
    // UPDATE - Show form for editing pet
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Optional<Pet> petOpt = petService.getEntityById(id);
            if (petOpt.isEmpty()) {
                model.addAttribute("error", "Pet not found");
                return "error";
            }
            
            Pet pet = petOpt.get();
            model.addAttribute("pet", pet);
            model.addAttribute("owners", ownerService.getAllEntities());
            model.addAttribute("pageTitle", "Edit Pet - " + pet.getName());
            model.addAttribute("formAction", "/pets/" + id);
            model.addAttribute("submitText", "Update Pet");
            
            return "pets/form";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load pet for editing: " + e.getMessage());
            return "error";
        }
    }
    
    // UPDATE - Process edit pet form
    @PostMapping("/{id}")
    public String updatePet(@PathVariable Long id, @ModelAttribute Pet pet, 
                           RedirectAttributes redirectAttributes) {
        try {
            pet.setPetId(id);
            Pet updatedPet = petService.updateEntity(pet);
            redirectAttributes.addFlashAttribute("success", 
                "Pet '" + updatedPet.getName() + "' has been updated successfully!");
            return "redirect:/pets/" + id;
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Pet not found");
            return "redirect:/pets";
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", "Validation error: " + e.getMessage());
            return "redirect:/pets/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update pet: " + e.getMessage());
            return "redirect:/pets/" + id + "/edit";
        }
    }
    
    // DELETE - Delete pet
    @PostMapping("/{id}/delete")
    public String deletePet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Pet> petOpt = petService.getEntityById(id);
            if (petOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Pet not found");
                return "redirect:/pets";
            }
            
            String petName = petOpt.get().getName();
            boolean deleted = petService.deleteEntity(id);
            
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", 
                    "Pet '" + petName + "' has been deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to delete pet");
            }
            
            return "redirect:/pets";
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete pet: " + e.getMessage());
            return "redirect:/pets/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete pet: " + e.getMessage());
            return "redirect:/pets/" + id;
        }
    }
    
    // SEARCH endpoint removed - search functionality integrated into main list method
    
    // STATISTICS - Pet statistics page
    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        try {
            System.out.println("=== DEBUG: Loading Pet Statistics ===");

            Map<String, Long> speciesStats = null;
            Map<String, Long> healthStats = null;
            long totalPets = 0;
            double averageAge = 0.0;
            int adultPets = 0;
            int petsNeedingVaccination = 0;

            try {
                speciesStats = petService.getSpeciesStatistics();
                System.out.println("DEBUG: Species stats loaded: " + speciesStats);
            } catch (Exception e) {
                System.out.println("DEBUG: Error loading species stats: " + e.getMessage());
                speciesStats = new java.util.HashMap<>();
            }

            try {
                healthStats = petService.getHealthStatusStatistics();
                System.out.println("DEBUG: Health stats loaded: " + healthStats);
            } catch (Exception e) {
                System.out.println("DEBUG: Error loading health stats: " + e.getMessage());
                healthStats = new java.util.HashMap<>();
            }

            try {
                totalPets = petService.getTotalCount();
                System.out.println("DEBUG: Total pets: " + totalPets);
            } catch (Exception e) {
                System.out.println("DEBUG: Error getting total pets: " + e.getMessage());
            }

            try {
                averageAge = petService.getAverageAge();
                System.out.println("DEBUG: Average age: " + averageAge);
            } catch (Exception e) {
                System.out.println("DEBUG: Error getting average age: " + e.getMessage());
            }

            try {
                adultPets = petService.getAdultPets().size();
                System.out.println("DEBUG: Adult pets: " + adultPets);
            } catch (Exception e) {
                System.out.println("DEBUG: Error getting adult pets: " + e.getMessage());
            }

            try {
                petsNeedingVaccination = petService.getPetsRequiringVaccination().size();
                System.out.println("DEBUG: Pets needing vaccination: " + petsNeedingVaccination);
            } catch (Exception e) {
                System.out.println("DEBUG: Error getting pets needing vaccination: " + e.getMessage());
            }

            model.addAttribute("speciesStats", speciesStats);
            model.addAttribute("healthStats", healthStats);
            model.addAttribute("totalPets", totalPets);
            model.addAttribute("averageAge", averageAge);
            model.addAttribute("adultPets", adultPets);
            model.addAttribute("petsNeedingVaccination", petsNeedingVaccination);
            model.addAttribute("pageTitle", "Pet Statistics");

            System.out.println("DEBUG: All model attributes set, returning template");
            return "pets/statistics";
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in showStatistics: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load statistics: " + e.getMessage());
            return "error";
        }
    }
    
    // BUSINESS OPERATION - Transfer pet to new owner
    @PostMapping("/{id}/transfer")
    public String transferPet(@PathVariable Long id, @RequestParam Long newOwnerId, 
                             RedirectAttributes redirectAttributes) {
        try {
            boolean transferred = petService.transferPetToNewOwner(id, newOwnerId);
            
            if (transferred) {
                redirectAttributes.addFlashAttribute("success", "Pet has been transferred successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to transfer pet");
            }
            
            return "redirect:/pets/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Transfer failed: " + e.getMessage());
            return "redirect:/pets/" + id;
        }
    }
    
    // BUSINESS OPERATION - Update health status
    @PostMapping("/{id}/health-status")
    public String updateHealthStatus(@PathVariable Long id, 
                                   @RequestParam String healthStatus,
                                   RedirectAttributes redirectAttributes) {
        try {
            Pet.HealthStatus status = Pet.HealthStatus.fromString(healthStatus);
            petService.updateHealthStatus(id, status);
            
            redirectAttributes.addFlashAttribute("success", "Health status updated successfully!");
            return "redirect:/pets/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update health status: " + e.getMessage());
            return "redirect:/pets/" + id;
        }
    }
}
