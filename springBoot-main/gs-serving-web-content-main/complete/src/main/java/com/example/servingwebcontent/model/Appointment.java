package com.example.servingwebcontent.model;

import com.example.servingwebcontent.exception.ValidationException;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Appointment entity following OOP principles
 * Extends BaseEntity for common functionality (Inheritance)
 * Implements proper Encapsulation with validation
 */
public class Appointment extends BaseEntity {
    
    // Private fields (Encapsulation)
    private Long appointmentId;
    private Long petId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentDate;
    private String serviceType;
    private String description;
    private AppointmentStatus status;
    private BigDecimal cost;
    private String veterinarian;
    private String notes;
    
    // Additional fields for display purposes
    private String petName;
    private String ownerName;
    
    // Enum for appointment status
    public enum AppointmentStatus {
        SCHEDULED("Scheduled"),
        CONFIRMED("Confirmed"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled"),
        NO_SHOW("No Show");
        
        private final String displayName;
        
        AppointmentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static AppointmentStatus fromString(String value) {
            if (value == null) return SCHEDULED;
            
            for (AppointmentStatus status : AppointmentStatus.values()) {
                if (status.displayName.equalsIgnoreCase(value) || 
                    status.name().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            return SCHEDULED;
        }
    }
    
    // Constructors
    public Appointment() {
        super();
        this.status = AppointmentStatus.SCHEDULED;
        this.cost = BigDecimal.ZERO;
    }
    
    public Appointment(Long petId, LocalDateTime appointmentDate, String serviceType) {
        this();
        this.setPetId(petId);
        this.setAppointmentDate(appointmentDate);
        this.setServiceType(serviceType);
    }
    
    // Override abstract method from BaseEntity
    @Override
    public Long getId() {
        return this.appointmentId;
    }
    
    // Encapsulation: Getters
    public Long getAppointmentId() {
        return appointmentId;
    }
    
    public Long getPetId() {
        return petId;
    }
    
    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }
    
    public String getServiceType() {
        return serviceType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public AppointmentStatus getStatus() {
        return status;
    }
    
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : AppointmentStatus.SCHEDULED.getDisplayName();
    }
    
    public BigDecimal getCost() {
        return cost;
    }
    
    public String getVeterinarian() {
        return veterinarian;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public String getPetName() {
        return petName;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    // Encapsulation: Setters with validation
    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public void setPetId(Long petId) {
        if (petId == null || petId <= 0) {
            throw new ValidationException("Pet ID must be positive");
        }
        this.petId = petId;
        this.updateTimestamp();
    }
    
    public void setAppointmentDate(LocalDateTime appointmentDate) {
        // Allow null during form binding, validation will happen later
        this.appointmentDate = appointmentDate;
        this.updateTimestamp();
    }

    // Separate method for validating new appointments
    public void validateForNewAppointment() {
        if (appointmentDate == null) {
            throw new ValidationException("Please select an appointment date and time");
        }
        if (appointmentDate.isBefore(LocalDateTime.now().minusHours(1))) {
            throw new ValidationException("Appointment date cannot be in the past");
        }
    }
    
    public void setServiceType(String serviceType) {
        if (serviceType == null || serviceType.trim().isEmpty()) {
            throw new ValidationException("Service type cannot be null or empty");
        }
        this.serviceType = serviceType.trim();
        this.updateTimestamp();
    }
    
    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
        this.updateTimestamp();
    }
    
    public void setStatus(AppointmentStatus status) {
        this.status = status != null ? status : AppointmentStatus.SCHEDULED;
        this.updateTimestamp();
    }
    
    public void setStatus(String statusString) {
        this.setStatus(AppointmentStatus.fromString(statusString));
    }
    
    public void setCost(BigDecimal cost) {
        if (cost != null && cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Cost cannot be negative");
        }
        this.cost = cost != null ? cost : BigDecimal.ZERO;
        this.updateTimestamp();
    }
    
    public void setCost(Double cost) {
        this.setCost(cost != null ? BigDecimal.valueOf(cost) : BigDecimal.ZERO);
    }
    
    public void setVeterinarian(String veterinarian) {
        this.veterinarian = veterinarian != null ? veterinarian.trim() : null;
        this.updateTimestamp();
    }
    
    public void setNotes(String notes) {
        this.notes = notes != null ? notes.trim() : null;
        this.updateTimestamp();
    }
    
    public void setPetName(String petName) {
        this.petName = petName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    // Business logic methods
    public boolean isUpcoming() {
        return appointmentDate != null &&
               appointmentDate.isAfter(LocalDateTime.now()) &&
               (status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.CONFIRMED);
    }
    
    public boolean isToday() {
        if (appointmentDate == null) return false;
        
        LocalDateTime now = LocalDateTime.now();
        return appointmentDate.toLocalDate().equals(now.toLocalDate());
    }
    
    public boolean canBeCancelled() {
        return (status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.CONFIRMED) &&
               appointmentDate != null &&
               appointmentDate.isAfter(LocalDateTime.now().plusHours(2));
    }
    
    public boolean canBeCompleted() {
        return status == AppointmentStatus.SCHEDULED ||
               status == AppointmentStatus.CONFIRMED ||
               status == AppointmentStatus.IN_PROGRESS;
    }
    
    public void markAsCompleted() {
        if (!canBeCompleted()) {
            throw new ValidationException("Appointment cannot be marked as completed");
        }
        this.status = AppointmentStatus.COMPLETED;
        this.updateTimestamp();
    }
    
    public void cancel() {
        if (!canBeCancelled()) {
            throw new ValidationException("Appointment cannot be cancelled");
        }
        this.status = AppointmentStatus.CANCELLED;
        this.updateTimestamp();
    }
    
    public String getFormattedDate() {
        if (appointmentDate == null) return "Not scheduled";
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return appointmentDate.format(formatter);
    }
    
    public String getFormattedCost() {
        if (cost == null || cost.equals(BigDecimal.ZERO)) {
            return "Free";
        }
        return String.format("%,.0f VNĐ", cost.doubleValue());
    }
    
    public String getDisplayInfo() {
        StringBuilder info = new StringBuilder();
        info.append(serviceType);
        if (petName != null) {
            info.append(" for ").append(petName);
        }
        if (appointmentDate != null) {
            info.append(" on ").append(getFormattedDate());
        }
        return info.toString();
    }
    
    // Override validation from BaseEntity (Polymorphism)
    @Override
    public boolean isValid() {
        // For new appointments, we don't require an ID yet (it will be set when saved)
        // Only check ID if it exists
        boolean idValid = appointmentId == null || appointmentId > 0;
        boolean petIdValid = petId != null && petId > 0;
        boolean dateValid = appointmentDate != null;
        boolean serviceValid = serviceType != null && !serviceType.trim().isEmpty();
        boolean statusValid = status != null;
        boolean costValid = cost != null && cost.compareTo(BigDecimal.ZERO) >= 0;

        System.out.println("DEBUG: Appointment.isValid() breakdown:");
        System.out.println("DEBUG: - id valid: " + idValid + " (appointmentId: " + appointmentId + ")");
        System.out.println("DEBUG: - petId valid: " + petIdValid + " (petId: " + petId + ")");
        System.out.println("DEBUG: - date valid: " + dateValid + " (date: " + appointmentDate + ")");
        System.out.println("DEBUG: - service valid: " + serviceValid + " (service: " + serviceType + ")");
        System.out.println("DEBUG: - status valid: " + statusValid + " (status: " + status + ")");
        System.out.println("DEBUG: - cost valid: " + costValid + " (cost: " + cost + ")");

        return idValid && petIdValid && dateValid && serviceValid && statusValid && costValid;
    }
    
    // Override equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Appointment that = (Appointment) obj;
        return Objects.equals(appointmentId, that.appointmentId) &&
               Objects.equals(petId, that.petId) &&
               Objects.equals(appointmentDate, that.appointmentDate) &&
               Objects.equals(serviceType, that.serviceType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(appointmentId, petId, appointmentDate, serviceType);
    }
    
    @Override
    public String toString() {
        return String.format("Appointment{id=%d, petId=%d, date=%s, service='%s', status=%s}", 
                           appointmentId, petId, getFormattedDate(), serviceType, status);
    }
}
