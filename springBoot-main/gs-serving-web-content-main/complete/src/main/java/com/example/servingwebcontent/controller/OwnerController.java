package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.exception.EntityNotFoundException;
import com.example.servingwebcontent.exception.ValidationException;
import com.example.servingwebcontent.model.Owner;
import com.example.servingwebcontent.model.Pet;
import com.example.servingwebcontent.service.OwnerServiceImpl;
import com.example.servingwebcontent.util.SearchValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Owner Controller following MVC pattern
 * Handles HTTP requests for Owner operations
 */
@Controller
@RequestMapping("/owners")
public class OwnerController {
    
    private final OwnerServiceImpl ownerService;
    
    @Autowired
    public OwnerController(OwnerServiceImpl ownerService) {
        this.ownerService = ownerService;
    }
    
    // READ - Display all owners with search functionality
    @GetMapping
    public String listOwners(@RequestParam(required = false) String query,
                             @RequestParam(required = false) String email,
                             Model model) {
        try {
            List<Owner> owners;
            String pageTitle = "Owner Management";

            // Handle search functionality with validation
            String validatedQuery = SearchValidationUtil.validateAndSanitizeQuery(query);
            String validatedEmail = SearchValidationUtil.validateEmail(email);

            if (validatedQuery != null) {
                owners = ownerService.searchEntities(validatedQuery);
                model.addAttribute("searchQuery", validatedQuery);
                pageTitle = "Search Results - " + owners.size() + " found";
            } else if (validatedEmail != null) {
                Optional<Owner> ownerOpt = ownerService.getOwnerByEmail(validatedEmail);
                owners = ownerOpt.map(List::of).orElse(List.of());
                model.addAttribute("searchEmail", validatedEmail);
                pageTitle = "Email Search - " + owners.size() + " found";
            } else {
                owners = ownerService.getAllEntities();
            }

            // Calculate statistics
            model.addAttribute("owners", owners);
            model.addAttribute("totalOwners", owners.size());
            model.addAttribute("ownersWithPets", ownerService.getOwnersWithPetsCount());
            model.addAttribute("averagePetsPerOwner", ownerService.getAveragePetsPerOwner());
            model.addAttribute("pageTitle", pageTitle);

            return "owners/list";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load owners: " + e.getMessage());
            return "error";
        }
    }
    
    // READ - Display single owner details
    @GetMapping("/{id}")
    public String viewOwner(@PathVariable Long id, Model model) {
        try {
            Optional<Owner> ownerOpt = ownerService.getEntityById(id);
            if (ownerOpt.isEmpty()) {
                model.addAttribute("error", "Owner not found");
                return "error";
            }
            
            Owner owner = ownerOpt.get();
            List<Pet> ownerPets = ownerService.getOwnerPets(id);
            
            model.addAttribute("owner", owner);
            model.addAttribute("pets", ownerPets);
            model.addAttribute("petCount", ownerPets.size());
            model.addAttribute("pageTitle", "Owner Details - " + owner.getName());
            
            return "owners/view";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load owner: " + e.getMessage());
            return "error";
        }
    }
    
    // CREATE - Show form for new owner
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("owner", new Owner());
            model.addAttribute("pageTitle", "Add New Owner");
            model.addAttribute("formAction", "/owners");
            model.addAttribute("submitText", "Add Owner");
            
            return "owners/form";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load form: " + e.getMessage());
            return "error";
        }
    }
    
    // CREATE - Process new owner form
    @PostMapping
    public String createOwner(@ModelAttribute Owner owner, RedirectAttributes redirectAttributes) {
        try {
            Owner savedOwner = ownerService.create(owner);
            redirectAttributes.addFlashAttribute("success", 
                "Owner '" + savedOwner.getName() + "' has been added successfully!");
            return "redirect:/owners/" + savedOwner.getOwnerId();
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", "Validation error: " + e.getMessage());
            return "redirect:/owners/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add owner: " + e.getMessage());
            return "redirect:/owners/new";
        }
    }
    
    // UPDATE - Show form for editing owner
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Optional<Owner> ownerOpt = ownerService.getEntityById(id);
            if (ownerOpt.isEmpty()) {
                model.addAttribute("error", "Owner not found");
                return "error";
            }
            
            Owner owner = ownerOpt.get();
            model.addAttribute("owner", owner);
            model.addAttribute("pageTitle", "Edit Owner - " + owner.getName());
            model.addAttribute("formAction", "/owners/" + id);
            model.addAttribute("submitText", "Update Owner");
            
            return "owners/form";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load owner for editing: " + e.getMessage());
            return "error";
        }
    }
    
    // UPDATE - Process edit owner form
    @PostMapping("/{id}")
    public String updateOwner(@PathVariable Long id, @ModelAttribute Owner owner, 
                             RedirectAttributes redirectAttributes) {
        try {
            owner.setOwnerId(id);
            Owner updatedOwner = ownerService.updateEntity(owner);
            redirectAttributes.addFlashAttribute("success", 
                "Owner '" + updatedOwner.getName() + "' has been updated successfully!");
            return "redirect:/owners/" + id;
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Owner not found");
            return "redirect:/owners";
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", "Validation error: " + e.getMessage());
            return "redirect:/owners/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update owner: " + e.getMessage());
            return "redirect:/owners/" + id + "/edit";
        }
    }
    
    // DELETE - Delete owner
    @PostMapping("/{id}/delete")
    public String deleteOwner(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Owner> ownerOpt = ownerService.getEntityById(id);
            if (ownerOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Owner not found");
                return "redirect:/owners";
            }
            
            String ownerName = ownerOpt.get().getName();
            boolean deleted = ownerService.deleteEntity(id);
            
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", 
                    "Owner '" + ownerName + "' has been deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to delete owner");
            }
            
            return "redirect:/owners";
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete owner: " + e.getMessage());
            return "redirect:/owners/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete owner: " + e.getMessage());
            return "redirect:/owners/" + id;
        }
    }
    
    // SEARCH endpoint removed - search functionality integrated into main list method
    
    // BUSINESS OPERATION - Transfer all pets to another owner
    @PostMapping("/{fromId}/transfer-pets/{toId}")
    public String transferAllPets(@PathVariable Long fromId, @PathVariable Long toId, 
                                 RedirectAttributes redirectAttributes) {
        try {
            boolean transferred = ownerService.transferAllPetsToNewOwner(fromId, toId);
            
            if (transferred) {
                redirectAttributes.addFlashAttribute("success", "All pets have been transferred successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to transfer pets");
            }
            
            return "redirect:/owners/" + fromId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Transfer failed: " + e.getMessage());
            return "redirect:/owners/" + fromId;
        }
    }
    
    // BUSINESS OPERATION - Update contact information
    @PostMapping("/{id}/update-contact")
    public String updateContact(@PathVariable Long id, 
                               @RequestParam String email,
                               @RequestParam String phone,
                               RedirectAttributes redirectAttributes) {
        try {
            boolean updated = ownerService.updateOwnerContact(id, email, phone);
            
            if (updated) {
                redirectAttributes.addFlashAttribute("success", "Contact information updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update contact information");
            }
            
            return "redirect:/owners/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Update failed: " + e.getMessage());
            return "redirect:/owners/" + id;
        }
    }
    
    // STATISTICS - Owners without pets
    @GetMapping("/without-pets")
    public String ownersWithoutPets(Model model) {
        try {
            List<Owner> owners = ownerService.getOwnersWithoutPets();
            
            model.addAttribute("owners", owners);
            model.addAttribute("pageTitle", "Owners Without Pets");
            model.addAttribute("totalResults", owners.size());
            
            return "owners/without-pets";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load data: " + e.getMessage());
            return "error";
        }
    }
    
    // VALIDATION - Check email availability
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmailAvailability(@RequestParam String email) {
        try {
            return ownerService.isEmailAvailable(email);
        } catch (Exception e) {
            return false;
        }
    }
}
