package com.example.servingwebcontent.service;

import com.example.servingwebcontent.exception.EntityNotFoundException;
import com.example.servingwebcontent.exception.ValidationException;
import com.example.servingwebcontent.model.Appointment;
import com.example.servingwebcontent.repository.AppointmentRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Appointment Service Implementation following OOP principles
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {
    
    private final AppointmentRepositoryImpl appointmentRepository;
    
    @Autowired
    public AppointmentServiceImpl(AppointmentRepositoryImpl appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }
    
    // Implement BaseService interface methods
    
    @Override
    public Appointment create(Appointment appointment) {
        if (appointment == null) {
            throw new ValidationException("Appointment cannot be null");
        }
        
        if (!isValidEntity(appointment)) {
            throw new ValidationException("Invalid appointment data");
        }

        // Validate for new appointments (check future date)
        appointment.validateForNewAppointment();

        // Business logic: Check time slot availability
        if (!isTimeSlotAvailable(appointment.getAppointmentDate(), appointment.getVeterinarian())) {
            throw new ValidationException("Time slot is not available");
        }

        // Set default status if not provided
        if (appointment.getStatus() == null) {
            appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        }
        
        return appointmentRepository.save(appointment);
    }
    
    @Override
    public List<Appointment> getAllEntities() {
        return appointmentRepository.findAll();
    }
    
    @Override
    public Optional<Appointment> getEntityById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return appointmentRepository.findById(id);
    }
    
    @Override
    public Appointment updateEntity(Appointment appointment) {
        if (appointment == null || appointment.getAppointmentId() == null) {
            throw new ValidationException("Appointment and Appointment ID cannot be null for update");
        }
        
        if (!appointmentRepository.existsById(appointment.getAppointmentId())) {
            throw new EntityNotFoundException("Appointment", appointment.getAppointmentId());
        }
        
        if (!isValidEntity(appointment)) {
            throw new ValidationException("Invalid appointment data for update");
        }
        
        // Business logic: Check if appointment can be modified
        if (!canAppointmentBeModified(appointment.getAppointmentId())) {
            throw new ValidationException("Appointment cannot be modified");
        }
        
        return appointmentRepository.update(appointment);
    }
    
    @Override
    public boolean deleteEntity(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Appointment", id);
        }
        
        // Business logic: Check if appointment can be cancelled
        if (!canAppointmentBeCancelled(id)) {
            throw new ValidationException("Appointment cannot be cancelled");
        }
        
        return appointmentRepository.deleteById(id);
    }
    
    @Override
    public long getTotalCount() {
        return appointmentRepository.count();
    }
    
    @Override
    public boolean entityExists(Long id) {
        return appointmentRepository.existsById(id);
    }
    
    @Override
    public List<Appointment> searchEntities(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllEntities();
        }
        return appointmentRepository.findByName(searchTerm.trim());
    }
    
    @Override
    public boolean isValidEntity(Appointment appointment) {
        if (appointment == null) {
            System.out.println("DEBUG: Appointment is null");
            return false;
        }

        // Use the entity's own validation method (Polymorphism)
        System.out.println("DEBUG: Checking appointment.isValid()...");
        if (!appointment.isValid()) {
            System.out.println("DEBUG: appointment.isValid() returned false");
            System.out.println("DEBUG: - petId: " + appointment.getPetId());
            System.out.println("DEBUG: - appointmentDate: " + appointment.getAppointmentDate());
            System.out.println("DEBUG: - serviceType: " + appointment.getServiceType());
            System.out.println("DEBUG: - status: " + appointment.getStatus());
            System.out.println("DEBUG: - cost: " + appointment.getCost());
            return false;
        }

        // Additional business validation
        if (appointment.getPetId() == null || appointment.getPetId() <= 0) {
            System.out.println("DEBUG: Invalid petId: " + appointment.getPetId());
            return false;
        }

        if (appointment.getAppointmentDate() == null) {
            System.out.println("DEBUG: appointmentDate is null");
            return false;
        }

        if (appointment.getServiceType() == null || appointment.getServiceType().trim().isEmpty()) {
            System.out.println("DEBUG: Invalid serviceType: " + appointment.getServiceType());
            return false;
        }

        System.out.println("DEBUG: All validations passed");
        // Don't validate past dates for existing appointments (only for new ones)
        return true;
    }
    
    // Implement AppointmentService specific methods
    
    @Override
    public List<Appointment> getAppointmentsByPet(Long petId) {
        if (petId == null || petId <= 0) {
            return List.of();
        }
        return appointmentRepository.findByPetId(petId);
    }
    
    @Override
    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        if (status == null) {
            return List.of();
        }
        return appointmentRepository.findByStatus(status.getDisplayName());
    }
    
    @Override
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return List.of();
        }
        return appointmentRepository.findByDateRange(startDate, endDate);
    }
    
    @Override
    public List<Appointment> getTodaysAppointments() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        return getAppointmentsByDateRange(startOfDay, endOfDay);
    }
    
    @Override
    public List<Appointment> getUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureLimit = now.plusMonths(3); // Next 3 months
        
        return getAllEntities().stream()
                .filter(appointment -> appointment.getAppointmentDate().isAfter(now) &&
                                     appointment.getAppointmentDate().isBefore(futureLimit) &&
                                     appointment.getStatus() == Appointment.AppointmentStatus.SCHEDULED)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Appointment> getPastAppointments() {
        LocalDateTime now = LocalDateTime.now();
        
        return getAllEntities().stream()
                .filter(appointment -> appointment.getAppointmentDate().isBefore(now))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean scheduleAppointment(Appointment appointment) {
        try {
            create(appointment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime) {
        Optional<Appointment> appointmentOpt = getEntityById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return false;
        }
        
        Appointment appointment = appointmentOpt.get();
        
        if (!canAppointmentBeModified(appointmentId)) {
            return false;
        }
        
        if (!isTimeSlotAvailable(newDateTime, appointment.getVeterinarian())) {
            return false;
        }
        
        appointment.setAppointmentDate(newDateTime);
        
        try {
            updateEntity(appointment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean cancelAppointment(Long appointmentId, String reason) {
        Optional<Appointment> appointmentOpt = getEntityById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return false;
        }
        
        if (!canAppointmentBeCancelled(appointmentId)) {
            return false;
        }
        
        Appointment appointment = appointmentOpt.get();
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        
        if (reason != null && !reason.trim().isEmpty()) {
            String currentNotes = appointment.getNotes() != null ? appointment.getNotes() : "";
            appointment.setNotes(currentNotes + "\nCancellation reason: " + reason.trim());
        }
        
        try {
            updateEntity(appointment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean completeAppointment(Long appointmentId, String notes) {
        Optional<Appointment> appointmentOpt = getEntityById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return false;
        }
        
        Appointment appointment = appointmentOpt.get();
        
        if (!appointment.canBeCompleted()) {
            return false;
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        
        if (notes != null && !notes.trim().isEmpty()) {
            String currentNotes = appointment.getNotes() != null ? appointment.getNotes() : "";
            appointment.setNotes(currentNotes + "\nCompletion notes: " + notes.trim());
        }
        
        try {
            updateEntity(appointment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean markAsNoShow(Long appointmentId) {
        Optional<Appointment> appointmentOpt = getEntityById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return false;
        }
        
        Appointment appointment = appointmentOpt.get();
        appointment.setStatus(Appointment.AppointmentStatus.NO_SHOW);
        
        try {
            updateEntity(appointment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isTimeSlotAvailable(LocalDateTime dateTime, String veterinarian) {
        if (dateTime == null) {
            return false;
        }
        
        // Check for conflicts within 1 hour window
        LocalDateTime startWindow = dateTime.minusMinutes(30);
        LocalDateTime endWindow = dateTime.plusMinutes(30);
        
        List<Appointment> conflictingAppointments = getAppointmentsByDateRange(startWindow, endWindow)
                .stream()
                .filter(appointment -> appointment.getStatus() == Appointment.AppointmentStatus.SCHEDULED)
                .filter(appointment -> veterinarian == null || 
                                     veterinarian.equals(appointment.getVeterinarian()))
                .collect(Collectors.toList());
        
        return conflictingAppointments.isEmpty();
    }
    
    @Override
    public boolean canAppointmentBeModified(Long appointmentId) {
        Optional<Appointment> appointmentOpt = getEntityById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return false;
        }

        Appointment appointment = appointmentOpt.get();

        // Can modify scheduled, confirmed, or in-progress appointments
        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED ||
            appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED ||
            appointment.getStatus() == Appointment.AppointmentStatus.NO_SHOW) {
            return false;
        }

        // Allow modification up to 30 minutes before appointment (more flexible for demo)
        return appointment.getAppointmentDate().isAfter(LocalDateTime.now().plusMinutes(30));
    }
    
    @Override
    public boolean canAppointmentBeCancelled(Long appointmentId) {
        Optional<Appointment> appointmentOpt = getEntityById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return false;
        }
        
        Appointment appointment = appointmentOpt.get();
        return appointment.canBeCancelled();
    }
    
    @Override
    public List<LocalDateTime> getAvailableTimeSlots(LocalDateTime date, String veterinarian) {
        // Generate time slots for the day (9 AM to 5 PM, every hour)
        List<LocalDateTime> timeSlots = java.util.stream.IntStream.range(9, 17)
                .mapToObj(hour -> date.toLocalDate().atTime(hour, 0))
                .collect(Collectors.toList());
        
        // Filter out unavailable slots
        return timeSlots.stream()
                .filter(slot -> isTimeSlotAvailable(slot, veterinarian))
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Long> getAppointmentStatusStatistics() {
        return getAllEntities().stream()
                .collect(Collectors.groupingBy(
                    appointment -> appointment.getStatus().getDisplayName(),
                    Collectors.counting()
                ));
    }
    
    @Override
    public Map<String, Long> getServiceTypeStatistics() {
        return getAllEntities().stream()
                .collect(Collectors.groupingBy(
                    Appointment::getServiceType,
                    Collectors.counting()
                ));
    }
    
    @Override
    public long getAppointmentsCountByPet(Long petId) {
        if (petId == null || petId <= 0) {
            return 0;
        }
        return getAppointmentsByPet(petId).size();
    }
    
    @Override
    public double getAverageAppointmentCost() {
        return getAllEntities().stream()
                .filter(appointment -> appointment.getCost() != null)
                .mapToDouble(appointment -> appointment.getCost().doubleValue())
                .average()
                .orElse(0.0);
    }
    
    @Override
    public List<Appointment> getAppointmentsNeedingReminders() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime dayAfterTomorrow = tomorrow.plusDays(1);
        
        return getAppointmentsByDateRange(tomorrow, dayAfterTomorrow)
                .stream()
                .filter(appointment -> appointment.getStatus() == Appointment.AppointmentStatus.SCHEDULED)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Appointment> getOverdueAppointments() {
        LocalDateTime now = LocalDateTime.now();
        
        return getAllEntities().stream()
                .filter(appointment -> appointment.getAppointmentDate().isBefore(now) &&
                                     appointment.getStatus() == Appointment.AppointmentStatus.SCHEDULED)
                .collect(Collectors.toList());
    }
}
