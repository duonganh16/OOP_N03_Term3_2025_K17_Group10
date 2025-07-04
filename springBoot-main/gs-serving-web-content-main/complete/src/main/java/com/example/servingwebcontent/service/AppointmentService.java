package com.example.servingwebcontent.service;

import com.example.servingwebcontent.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Appointment Service Interface following SOLID principles
 * Defines business logic contract for Appointment operations
 */
public interface AppointmentService extends BaseService<Appointment, Long> {
    
    // Appointment-specific business operations
    List<Appointment> getAppointmentsByPet(Long petId);
    List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status);
    List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Appointment> getTodaysAppointments();
    List<Appointment> getUpcomingAppointments();
    List<Appointment> getPastAppointments();
    
    // Business logic operations
    boolean scheduleAppointment(Appointment appointment);
    boolean rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime);
    boolean cancelAppointment(Long appointmentId, String reason);
    boolean completeAppointment(Long appointmentId, String notes);
    boolean markAsNoShow(Long appointmentId);
    
    // Validation and business rules
    boolean isTimeSlotAvailable(LocalDateTime dateTime, String veterinarian);
    boolean canAppointmentBeModified(Long appointmentId);
    boolean canAppointmentBeCancelled(Long appointmentId);
    List<LocalDateTime> getAvailableTimeSlots(LocalDateTime date, String veterinarian);
    
    // Statistics and reporting
    Map<String, Long> getAppointmentStatusStatistics();
    Map<String, Long> getServiceTypeStatistics();
    long getAppointmentsCountByPet(Long petId);
    double getAverageAppointmentCost();
    
    // Notification and reminders
    List<Appointment> getAppointmentsNeedingReminders();
    List<Appointment> getOverdueAppointments();
}
