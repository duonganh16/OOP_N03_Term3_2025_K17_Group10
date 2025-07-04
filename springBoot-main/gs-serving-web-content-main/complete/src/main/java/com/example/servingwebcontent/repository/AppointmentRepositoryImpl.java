package com.example.servingwebcontent.repository;

import com.example.servingwebcontent.constants.DatabaseConstants;
import com.example.servingwebcontent.exception.DatabaseException;
import com.example.servingwebcontent.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Appointment Repository Implementation following OOP principles
 */
@Repository
public class AppointmentRepositoryImpl implements BaseRepository<Appointment, Long> {
    
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Appointment> appointmentRowMapper;
    
    @Autowired
    public AppointmentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.appointmentRowMapper = new AppointmentRowMapper();
    }
    
    private static class AppointmentRowMapper implements RowMapper<Appointment> {
        @Override
        public Appointment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(rs.getLong(DatabaseConstants.COLUMN_APPOINTMENT_ID));
            appointment.setPetId(rs.getLong("pet_id"));
            
            if (rs.getTimestamp(DatabaseConstants.COLUMN_APPOINTMENT_DATE) != null) {
                appointment.setAppointmentDate(rs.getTimestamp(DatabaseConstants.COLUMN_APPOINTMENT_DATE).toLocalDateTime());
            }
            
            appointment.setServiceType(rs.getString(DatabaseConstants.COLUMN_SERVICE_TYPE));
            appointment.setDescription(rs.getString(DatabaseConstants.COLUMN_DESCRIPTION));
            appointment.setStatus(rs.getString(DatabaseConstants.COLUMN_STATUS));
            appointment.setCost(rs.getDouble(DatabaseConstants.COLUMN_COST));
            appointment.setVeterinarian(rs.getString(DatabaseConstants.COLUMN_VETERINARIAN));
            appointment.setNotes(rs.getString(DatabaseConstants.COLUMN_NOTES));
            
            if (rs.getTimestamp(DatabaseConstants.COLUMN_CREATED_AT) != null) {
                appointment.setCreatedAt(rs.getTimestamp(DatabaseConstants.COLUMN_CREATED_AT).toLocalDateTime());
            }
            
            // Handle additional fields if present
            try {
                appointment.setPetName(rs.getString("pet_name"));
                appointment.setOwnerName(rs.getString("owner_name"));
            } catch (SQLException e) {
                // These fields might not be present in all queries
            }
            
            return appointment;
        }
    }
    
    @Override
    public Appointment save(Appointment appointment) {
        if (appointment == null || !appointment.isValid()) {
            throw new DatabaseException("Invalid appointment data", null);
        }
        
        try {
            String sql = "INSERT INTO " + DatabaseConstants.TABLE_APPOINTMENTS + 
                        " (pet_id, " + DatabaseConstants.COLUMN_APPOINTMENT_DATE + ", " + 
                        DatabaseConstants.COLUMN_SERVICE_TYPE + ", " + DatabaseConstants.COLUMN_DESCRIPTION + ", " +
                        DatabaseConstants.COLUMN_STATUS + ", " + DatabaseConstants.COLUMN_COST + ", " +
                        DatabaseConstants.COLUMN_VETERINARIAN + ", " + DatabaseConstants.COLUMN_NOTES + 
                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, appointment.getPetId());
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(appointment.getAppointmentDate()));
                ps.setString(3, appointment.getServiceType());
                ps.setString(4, appointment.getDescription());
                ps.setString(5, appointment.getStatusDisplayName());
                ps.setBigDecimal(6, appointment.getCost());
                ps.setString(7, appointment.getVeterinarian());
                ps.setString(8, appointment.getNotes());
                return ps;
            }, keyHolder);
            
            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                appointment.setAppointmentId(generatedId.longValue());
            }
            
            return appointment;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to save appointment: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Appointment> findAll() {
        try {
            String sql = "SELECT a.*, p.name as pet_name, o.name as owner_name " +
                        "FROM " + DatabaseConstants.TABLE_APPOINTMENTS + " a " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_PETS + " p ON a.pet_id = p.pet_id " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_OWNERS + " o ON p.owner_id = o.owner_id " +
                        "ORDER BY a." + DatabaseConstants.COLUMN_APPOINTMENT_DATE + " DESC";
            
            return jdbcTemplate.query(sql, appointmentRowMapper);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to retrieve all appointments: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Appointment> findById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        
        try {
            String sql = "SELECT a.*, p.name as pet_name, o.name as owner_name " +
                        "FROM " + DatabaseConstants.TABLE_APPOINTMENTS + " a " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_PETS + " p ON a.pet_id = p.pet_id " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_OWNERS + " o ON p.owner_id = o.owner_id " +
                        "WHERE a." + DatabaseConstants.COLUMN_APPOINTMENT_ID + " = ?";
            
            List<Appointment> appointments = jdbcTemplate.query(sql, appointmentRowMapper, id);
            return appointments.isEmpty() ? Optional.empty() : Optional.of(appointments.get(0));
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to find appointment by ID: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Appointment update(Appointment appointment) {
        if (appointment == null || appointment.getAppointmentId() == null || !appointment.isValid()) {
            throw new DatabaseException("Invalid appointment data for update", null);
        }
        
        try {
            String sql = "UPDATE " + DatabaseConstants.TABLE_APPOINTMENTS + " SET " +
                        "pet_id = ?, " + DatabaseConstants.COLUMN_APPOINTMENT_DATE + " = ?, " +
                        DatabaseConstants.COLUMN_SERVICE_TYPE + " = ?, " + DatabaseConstants.COLUMN_DESCRIPTION + " = ?, " +
                        DatabaseConstants.COLUMN_STATUS + " = ?, " + DatabaseConstants.COLUMN_COST + " = ?, " +
                        DatabaseConstants.COLUMN_VETERINARIAN + " = ?, " + DatabaseConstants.COLUMN_NOTES + " = ? " +
                        "WHERE " + DatabaseConstants.COLUMN_APPOINTMENT_ID + " = ?";
            
            int rowsAffected = jdbcTemplate.update(sql,
                appointment.getPetId(),
                java.sql.Timestamp.valueOf(appointment.getAppointmentDate()),
                appointment.getServiceType(),
                appointment.getDescription(),
                appointment.getStatusDisplayName(),
                appointment.getCost(),
                appointment.getVeterinarian(),
                appointment.getNotes(),
                appointment.getAppointmentId());
            
            if (rowsAffected == 0) {
                throw new DatabaseException("Appointment not found for update", null);
            }
            
            appointment.updateTimestamp();
            return appointment;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update appointment: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        try {
            String sql = "DELETE FROM " + DatabaseConstants.TABLE_APPOINTMENTS + 
                        " WHERE " + DatabaseConstants.COLUMN_APPOINTMENT_ID + " = ?";
            
            int rowsAffected = jdbcTemplate.update(sql, id);
            return rowsAffected > 0;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete appointment: " + e.getMessage(), e);
        }
    }
    
    @Override
    public long count() {
        try {
            String sql = "SELECT COUNT(*) FROM " + DatabaseConstants.TABLE_APPOINTMENTS;
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to count appointments: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        try {
            String sql = "SELECT COUNT(*) FROM " + DatabaseConstants.TABLE_APPOINTMENTS + 
                        " WHERE " + DatabaseConstants.COLUMN_APPOINTMENT_ID + " = ?";
            
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
            return count != null && count > 0;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to check appointment existence: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Appointment> findByName(String name) {
        // For appointments, search by service type
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            String sql = "SELECT a.*, p.name as pet_name, o.name as owner_name " +
                        "FROM " + DatabaseConstants.TABLE_APPOINTMENTS + " a " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_PETS + " p ON a.pet_id = p.pet_id " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_OWNERS + " o ON p.owner_id = o.owner_id " +
                        "WHERE a." + DatabaseConstants.COLUMN_SERVICE_TYPE + " LIKE ? " +
                        "ORDER BY a." + DatabaseConstants.COLUMN_APPOINTMENT_DATE + " DESC";
            
            return jdbcTemplate.query(sql, appointmentRowMapper, "%" + name.trim() + "%");
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to search appointments: " + e.getMessage(), e);
        }
    }
    
    // Additional business-specific methods
    public List<Appointment> findByPetId(Long petId) {
        if (petId == null || petId <= 0) {
            return List.of();
        }
        
        try {
            String sql = "SELECT a.*, p.name as pet_name, o.name as owner_name " +
                        "FROM " + DatabaseConstants.TABLE_APPOINTMENTS + " a " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_PETS + " p ON a.pet_id = p.pet_id " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_OWNERS + " o ON p.owner_id = o.owner_id " +
                        "WHERE a.pet_id = ? ORDER BY a." + DatabaseConstants.COLUMN_APPOINTMENT_DATE + " DESC";
            
            return jdbcTemplate.query(sql, appointmentRowMapper, petId);
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to find appointments by pet: " + e.getMessage(), e);
        }
    }
    
    public List<Appointment> findByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            String sql = "SELECT a.*, p.name as pet_name, o.name as owner_name " +
                        "FROM " + DatabaseConstants.TABLE_APPOINTMENTS + " a " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_PETS + " p ON a.pet_id = p.pet_id " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_OWNERS + " o ON p.owner_id = o.owner_id " +
                        "WHERE a." + DatabaseConstants.COLUMN_STATUS + " = ? " +
                        "ORDER BY a." + DatabaseConstants.COLUMN_APPOINTMENT_DATE + " DESC";
            
            return jdbcTemplate.query(sql, appointmentRowMapper, status.trim());
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to find appointments by status: " + e.getMessage(), e);
        }
    }
    
    public List<Appointment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return List.of();
        }
        
        try {
            String sql = "SELECT a.*, p.name as pet_name, o.name as owner_name " +
                        "FROM " + DatabaseConstants.TABLE_APPOINTMENTS + " a " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_PETS + " p ON a.pet_id = p.pet_id " +
                        "LEFT JOIN " + DatabaseConstants.TABLE_OWNERS + " o ON p.owner_id = o.owner_id " +
                        "WHERE a." + DatabaseConstants.COLUMN_APPOINTMENT_DATE + " BETWEEN ? AND ? " +
                        "ORDER BY a." + DatabaseConstants.COLUMN_APPOINTMENT_DATE + " ASC";
            
            return jdbcTemplate.query(sql, appointmentRowMapper, 
                java.sql.Timestamp.valueOf(startDate), 
                java.sql.Timestamp.valueOf(endDate));
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to find appointments by date range: " + e.getMessage(), e);
        }
    }
}
