package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.exception.EntityNotFoundException;
import com.example.servingwebcontent.exception.ValidationException;
import com.example.servingwebcontent.model.Appointment;
import com.example.servingwebcontent.service.AppointmentServiceImpl;
import com.example.servingwebcontent.service.PetServiceImpl;
import com.example.servingwebcontent.util.SearchValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Appointment Controller following MVC pattern
 * Handles HTTP requests for Appointment operations
 */
@Controller
@RequestMapping("/appointments")
public class AppointmentController {
    
    private final AppointmentServiceImpl appointmentService;
    private final PetServiceImpl petService;
    
    @Autowired
    public AppointmentController(AppointmentServiceImpl appointmentService, PetServiceImpl petService) {
        this.appointmentService = appointmentService;
        this.petService = petService;
    }

    // Configure data binding for LocalDateTime
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Spring will automatically use @DateTimeFormat annotation
        // No custom editor needed
    }

    // Helper method to provide specific error messages for modification restrictions
    private String getModificationErrorMessage(Appointment appointment) {
        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            return "Cannot modify completed appointments";
        }
        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            return "Cannot modify cancelled appointments";
        }
        if (appointment.getStatus() == Appointment.AppointmentStatus.NO_SHOW) {
            return "Cannot modify no-show appointments";
        }
        if (appointment.getAppointmentDate().isBefore(LocalDateTime.now().plusMinutes(30))) {
            return "Cannot modify appointments less than 30 minutes before the scheduled time";
        }
        return "This appointment cannot be modified";
    }
    
    // READ - Display all appointments with search functionality
    @GetMapping
    public String listAppointments(@RequestParam(required = false) String query,
                                  @RequestParam(required = false) Long petId,
                                  Model model) {
        try {
            List<Appointment> appointments;
            String pageTitle = "Appointment Management";

            // Handle search functionality with validation
            String validatedQuery = SearchValidationUtil.validateAndSanitizeQuery(query);
            Long validatedPetId = SearchValidationUtil.validatePetId(petId);

            if (validatedQuery != null) {
                appointments = appointmentService.searchEntities(validatedQuery);
                model.addAttribute("searchQuery", validatedQuery);
                pageTitle = "Search Results - " + appointments.size() + " found";
            } else if (validatedPetId != null) {
                appointments = appointmentService.getAppointmentsByPet(validatedPetId);
                model.addAttribute("searchPetId", validatedPetId);
                // Get pet name for display
                petService.getEntityById(validatedPetId).ifPresent(pet -> {
                    model.addAttribute("searchPetName", pet.getName());
                });
                pageTitle = "Appointments for Pet - " + appointments.size() + " found";
            } else {
                appointments = appointmentService.getAllEntities();
            }

            // Calculate statistics
            Map<String, Long> statusStats = appointmentService.getAppointmentStatusStatistics();
            Map<String, Long> serviceStats = appointmentService.getServiceTypeStatistics();
            double averageCost = appointmentService.getAverageAppointmentCost();

            // Debug logging - TODO: Replace with proper logging framework (SLF4J + Logback)
            // HƯỚNG DẪN THAY THẾ: Thay System.out.println bằng logger.debug()
            // Example: private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
            // logger.debug("=== DEBUG: Appointment Statistics ===");
            System.out.println("=== DEBUG: Appointment Statistics ===");
            System.out.println("Total appointments: " + appointments.size());
            System.out.println("Status stats: " + statusStats);
            System.out.println("Service stats: " + serviceStats);
            System.out.println("Average cost: " + averageCost);

            model.addAttribute("appointments", appointments);
            model.addAttribute("statusStats", statusStats);
            model.addAttribute("serviceStats", serviceStats);
            model.addAttribute("totalAppointments", appointments.size());
            model.addAttribute("averageCost", averageCost);
            model.addAttribute("pageTitle", pageTitle);

            // Add pets for the search dropdown
            model.addAttribute("pets", petService.getAllEntities());

            return "appointments/list";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load appointments: " + e.getMessage());
            return "error";
        }
    }
    
    // READ - Display single appointment details
    @GetMapping("/{id}")
    public String viewAppointment(@PathVariable Long id, Model model) {
        try {
            Optional<Appointment> appointmentOpt = appointmentService.getEntityById(id);
            if (appointmentOpt.isEmpty()) {
                model.addAttribute("error", "Appointment not found");
                return "error";
            }
            
            Appointment appointment = appointmentOpt.get();
            model.addAttribute("appointment", appointment);
            model.addAttribute("pageTitle", "Appointment Details");
            
            // Add pet information
            if (appointment.getPetId() != null) {
                petService.getEntityById(appointment.getPetId())
                          .ifPresent(pet -> model.addAttribute("pet", pet));
            }
            
            return "appointments/view";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load appointment: " + e.getMessage());
            return "error";
        }
    }
    
    // CREATE - Show form for new appointment
    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long petId, Model model) {
        try {
            Appointment appointment = new Appointment();
            if (petId != null) {
                appointment.setPetId(petId);
            }
            
            model.addAttribute("appointment", appointment);
            model.addAttribute("pets", petService.getAllEntities());
            model.addAttribute("pageTitle", "Schedule New Appointment");
            model.addAttribute("formAction", "/appointments");
            model.addAttribute("submitText", "Schedule Appointment");
            
            return "appointments/form";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load form: " + e.getMessage());
            return "error";
        }
    }
    
    // CREATE - Process new appointment form
    @PostMapping
    public String createAppointment(@ModelAttribute Appointment appointment,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Debug logging
            System.out.println("DEBUG: Received appointment: " + appointment);
            System.out.println("DEBUG: Appointment date: " + appointment.getAppointmentDate());
            System.out.println("DEBUG: Pet ID: " + appointment.getPetId());
            System.out.println("DEBUG: Service Type: " + appointment.getServiceType());
            System.out.println("DEBUG: Status: " + appointment.getStatus());
            System.out.println("DEBUG: Cost: " + appointment.getCost());
            System.out.println("DEBUG: Veterinarian: " + appointment.getVeterinarian());

            // Ensure cost is not null
            if (appointment.getCost() == null) {
                appointment.setCost(BigDecimal.ZERO);
                System.out.println("DEBUG: Set cost to ZERO");
            }

            // Ensure status is not null
            if (appointment.getStatus() == null) {
                appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
                System.out.println("DEBUG: Set status to SCHEDULED");
            }

            // Validate appointment date
            if (appointment.getAppointmentDate() == null) {
                redirectAttributes.addFlashAttribute("error",
                    "Please select a valid appointment date and time.");
                return "redirect:/appointments/new";
            }

            // Check if appointment is valid before proceeding
            System.out.println("DEBUG: Checking appointment validity...");
            System.out.println("DEBUG: appointment.isValid() = " + appointment.isValid());

            // Validate for new appointment (future date)
            appointment.validateForNewAppointment();

            Appointment savedAppointment = appointmentService.create(appointment);
            redirectAttributes.addFlashAttribute("success",
                "Appointment has been scheduled successfully!");
            return "redirect:/appointments/" + savedAppointment.getAppointmentId();
        } catch (ValidationException e) {
            System.out.println("DEBUG: ValidationException caught: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Validation error: " + e.getMessage());
            return "redirect:/appointments/new";
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in createAppointment: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to schedule appointment: " + e.getMessage());
            return "redirect:/appointments/new";
        }
    }
    
    // UPDATE - Show form for editing appointment
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Optional<Appointment> appointmentOpt = appointmentService.getEntityById(id);
            if (appointmentOpt.isEmpty()) {
                model.addAttribute("error", "Appointment not found");
                return "error";
            }
            
            Appointment appointment = appointmentOpt.get();
            
            // Check if appointment can be modified
            if (!appointmentService.canAppointmentBeModified(id)) {
                String errorMessage = getModificationErrorMessage(appointment);
                model.addAttribute("error", errorMessage);
                return "error";
            }
            
            model.addAttribute("appointment", appointment);
            model.addAttribute("pets", petService.getAllEntities());
            model.addAttribute("pageTitle", "Edit Appointment");
            model.addAttribute("formAction", "/appointments/" + id);
            model.addAttribute("submitText", "Update Appointment");
            
            return "appointments/form";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load appointment for editing: " + e.getMessage());
            return "error";
        }
    }
    
    // UPDATE - Process edit appointment form
    @PostMapping("/{id}")
    public String updateAppointment(@PathVariable Long id, @ModelAttribute Appointment appointment, 
                                   RedirectAttributes redirectAttributes) {
        try {
            appointment.setAppointmentId(id);
            Appointment updatedAppointment = appointmentService.updateEntity(appointment);
            redirectAttributes.addFlashAttribute("success", 
                "Appointment has been updated successfully!");
            return "redirect:/appointments/" + id;
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Appointment not found");
            return "redirect:/appointments";
        } catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("error", "Validation error: " + e.getMessage());
            return "redirect:/appointments/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update appointment: " + e.getMessage());
            return "redirect:/appointments/" + id + "/edit";
        }
    }
    
    // DELETE - Cancel appointment
    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, 
                                   @RequestParam(required = false) String reason,
                                   RedirectAttributes redirectAttributes) {
        try {
            boolean cancelled = appointmentService.cancelAppointment(id, reason);
            
            if (cancelled) {
                redirectAttributes.addFlashAttribute("success", "Appointment has been cancelled successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to cancel appointment");
            }
            
            return "redirect:/appointments/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel appointment: " + e.getMessage());
            return "redirect:/appointments/" + id;
        }
    }
    
    // BUSINESS OPERATION - Complete appointment
    @PostMapping("/{id}/complete")
    public String completeAppointment(@PathVariable Long id, 
                                     @RequestParam(required = false) String notes,
                                     RedirectAttributes redirectAttributes) {
        try {
            boolean completed = appointmentService.completeAppointment(id, notes);
            
            if (completed) {
                redirectAttributes.addFlashAttribute("success", "Appointment has been marked as completed!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to complete appointment");
            }
            
            return "redirect:/appointments/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to complete appointment: " + e.getMessage());
            return "redirect:/appointments/" + id;
        }
    }
    
    // BUSINESS OPERATION - Reschedule appointment
    @PostMapping("/{id}/reschedule")
    public String rescheduleAppointment(@PathVariable Long id, 
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime,
                                       RedirectAttributes redirectAttributes) {
        try {
            boolean rescheduled = appointmentService.rescheduleAppointment(id, newDateTime);
            
            if (rescheduled) {
                redirectAttributes.addFlashAttribute("success", "Appointment has been rescheduled successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to reschedule appointment - time slot may not be available");
            }
            
            return "redirect:/appointments/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reschedule appointment: " + e.getMessage());
            return "redirect:/appointments/" + id;
        }
    }
    
    // BUSINESS OPERATION - Mark as no show
    @PostMapping("/{id}/no-show")
    public String markAsNoShow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean marked = appointmentService.markAsNoShow(id);
            
            if (marked) {
                redirectAttributes.addFlashAttribute("success", "Appointment has been marked as no-show!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to mark appointment as no-show");
            }
            
            return "redirect:/appointments/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to mark as no-show: " + e.getMessage());
            return "redirect:/appointments/" + id;
        }
    }
    
    // VIEW - Today's appointments
    @GetMapping("/today")
    public String todaysAppointments(Model model) {
        try {
            List<Appointment> appointments = appointmentService.getTodaysAppointments();
            
            model.addAttribute("appointments", appointments);
            model.addAttribute("pageTitle", "Today's Appointments");
            model.addAttribute("totalAppointments", appointments.size());
            
            return "appointments/today";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load today's appointments: " + e.getMessage());
            return "error";
        }
    }
    
    // VIEW - Upcoming appointments
    @GetMapping("/upcoming")
    public String upcomingAppointments(Model model) {
        try {
            List<Appointment> appointments = appointmentService.getUpcomingAppointments();
            
            model.addAttribute("appointments", appointments);
            model.addAttribute("pageTitle", "Upcoming Appointments");
            model.addAttribute("totalAppointments", appointments.size());
            
            return "appointments/upcoming";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load upcoming appointments: " + e.getMessage());
            return "error";
        }
    }
    
    // VIEW - Appointments by status
    @GetMapping("/status/{status}")
    public String appointmentsByStatus(@PathVariable String status, Model model) {
        try {
            Appointment.AppointmentStatus appointmentStatus = Appointment.AppointmentStatus.fromString(status);
            List<Appointment> appointments = appointmentService.getAppointmentsByStatus(appointmentStatus);
            
            model.addAttribute("appointments", appointments);
            model.addAttribute("pageTitle", status + " Appointments");
            model.addAttribute("totalAppointments", appointments.size());
            model.addAttribute("status", status);
            
            return "appointments/by-status";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load appointments: " + e.getMessage());
            return "error";
        }
    }
    
    // SEARCH endpoint removed - search functionality integrated into main list method
    
    // API - Check time slot availability
    @GetMapping("/check-availability")
    @ResponseBody
    public boolean checkTimeSlotAvailability(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
                                            @RequestParam(required = false) String veterinarian) {
        try {
            return appointmentService.isTimeSlotAvailable(dateTime, veterinarian);
        } catch (Exception e) {
            return false;
        }
    }
}
