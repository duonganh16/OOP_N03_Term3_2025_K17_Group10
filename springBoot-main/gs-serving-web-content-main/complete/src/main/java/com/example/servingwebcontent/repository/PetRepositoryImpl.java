package com.example.servingwebcontent.repository;

import com.example.servingwebcontent.constants.DatabaseConstants;
import com.example.servingwebcontent.exception.DatabaseException;
import com.example.servingwebcontent.model.Pet;
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
import java.util.List;
import java.util.Optional;

/**
 * Pet Repository Implementation following OOP principles
 * Implements BaseRepository interface (Abstraction)
 * Uses Spring JDBC for database operations
 */
@Repository
public class PetRepositoryImpl implements BaseRepository<Pet, Long> {
    
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Pet> petRowMapper;
    
    // Constructor injection (Dependency Injection)
    @Autowired
    public PetRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.petRowMapper = new PetRowMapper();
    }
    
    // Inner class for row mapping (Single Responsibility)
    private static class PetRowMapper implements RowMapper<Pet> {
        @Override
        public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
            Pet pet = new Pet();
            pet.setPetId(rs.getLong(DatabaseConstants.COLUMN_PET_ID));
            pet.setName(rs.getString(DatabaseConstants.COLUMN_NAME));
            pet.setSpecies(rs.getString(DatabaseConstants.COLUMN_SPECIES));
            pet.setBreed(rs.getString(DatabaseConstants.COLUMN_BREED));
            
            // Handle nullable integers
            int age = rs.getInt(DatabaseConstants.COLUMN_AGE);
            pet.setAge(rs.wasNull() ? null : age);
            
            // Handle nullable doubles
            double weight = rs.getDouble(DatabaseConstants.COLUMN_WEIGHT);
            pet.setWeight(rs.wasNull() ? null : weight);
            
            pet.setColor(rs.getString(DatabaseConstants.COLUMN_COLOR));
            pet.setGender(rs.getString(DatabaseConstants.COLUMN_GENDER));
            
            // Handle nullable owner ID
            long ownerId = rs.getLong(DatabaseConstants.COLUMN_OWNER_ID);
            pet.setOwnerId(rs.wasNull() ? null : ownerId);
            
            pet.setHealthStatus(rs.getString(DatabaseConstants.COLUMN_HEALTH_STATUS));
            
            // Handle timestamps
            if (rs.getTimestamp(DatabaseConstants.COLUMN_CREATED_AT) != null) {
                pet.setCreatedAt(rs.getTimestamp(DatabaseConstants.COLUMN_CREATED_AT).toLocalDateTime());
            }
            
            // Handle additional fields if present
            try {
                pet.setOwnerName(rs.getString("owner_name"));
                pet.setOwnerPhone(rs.getString("owner_phone"));
            } catch (SQLException e) {
                // These fields might not be present in all queries
            }
            
            return pet;
        }
    }
    
    // Implement BaseRepository interface methods
    
    @Override
    public Pet save(Pet pet) {
        if (pet == null) {
            throw new DatabaseException("Cannot save null pet", null);
        }
        
        if (!pet.isValid()) {
            throw new DatabaseException("Invalid pet data", null);
        }
        
        try {
            String sql = buildInsertSql();
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                setPetParameters(ps, pet);
                return ps;
            }, keyHolder);
            
            // Set the generated ID
            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                pet.setPetId(generatedId.longValue());
            }
            
            return pet;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to save pet: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Pet> findAll() {
        try {
            String sql = "SELECT * FROM " + DatabaseConstants.TABLE_PETS + " ORDER BY " + DatabaseConstants.COLUMN_NAME;
            return jdbcTemplate.query(sql, petRowMapper);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to retrieve all pets: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Pet> findById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        
        try {
            String sql = "SELECT * FROM " + DatabaseConstants.TABLE_PETS + 
                        " WHERE " + DatabaseConstants.COLUMN_PET_ID + " = ?";
            
            List<Pet> pets = jdbcTemplate.query(sql, petRowMapper, id);
            return pets.isEmpty() ? Optional.empty() : Optional.of(pets.get(0));
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to find pet by ID: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Pet update(Pet pet) {
        if (pet == null || pet.getPetId() == null) {
            throw new DatabaseException("Cannot update pet without ID", null);
        }
        
        if (!pet.isValid()) {
            throw new DatabaseException("Invalid pet data for update", null);
        }
        
        try {
            String sql = buildUpdateSql();
            int rowsAffected = jdbcTemplate.update(sql, 
                pet.getName(), pet.getSpecies(), pet.getBreed(), pet.getAge(),
                pet.getWeight(), pet.getColor(), pet.getGenderDisplayName(), 
                pet.getHealthStatusDisplayName(), pet.getOwnerId(), pet.getPetId());
            
            if (rowsAffected == 0) {
                throw new DatabaseException("Pet not found for update", null);
            }
            
            pet.updateTimestamp();
            return pet;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update pet: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        try {
            String sql = "DELETE FROM " + DatabaseConstants.TABLE_PETS + 
                        " WHERE " + DatabaseConstants.COLUMN_PET_ID + " = ?";
            
            int rowsAffected = jdbcTemplate.update(sql, id);
            return rowsAffected > 0;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete pet: " + e.getMessage(), e);
        }
    }
    
    @Override
    public long count() {
        try {
            String sql = "SELECT COUNT(*) FROM " + DatabaseConstants.TABLE_PETS;
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to count pets: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        try {
            String sql = "SELECT COUNT(*) FROM " + DatabaseConstants.TABLE_PETS + 
                        " WHERE " + DatabaseConstants.COLUMN_PET_ID + " = ?";
            
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
            return count != null && count > 0;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to check pet existence: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Pet> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            String sql = "SELECT * FROM " + DatabaseConstants.TABLE_PETS + 
                        " WHERE " + DatabaseConstants.COLUMN_NAME + " LIKE ? ORDER BY " + DatabaseConstants.COLUMN_NAME;
            
            return jdbcTemplate.query(sql, petRowMapper, "%" + name.trim() + "%");
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to search pets by name: " + e.getMessage(), e);
        }
    }
    
    // Additional business-specific methods
    public List<Pet> findByOwnerId(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            return List.of();
        }
        
        try {
            String sql = "SELECT * FROM " + DatabaseConstants.TABLE_PETS + 
                        " WHERE " + DatabaseConstants.COLUMN_OWNER_ID + " = ? ORDER BY " + DatabaseConstants.COLUMN_NAME;
            
            return jdbcTemplate.query(sql, petRowMapper, ownerId);
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to find pets by owner: " + e.getMessage(), e);
        }
    }
    
    public List<Pet> findBySpecies(String species) {
        if (species == null || species.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            String sql = "SELECT * FROM " + DatabaseConstants.TABLE_PETS + 
                        " WHERE " + DatabaseConstants.COLUMN_SPECIES + " = ? ORDER BY " + DatabaseConstants.COLUMN_NAME;
            
            return jdbcTemplate.query(sql, petRowMapper, species.trim());
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to find pets by species: " + e.getMessage(), e);
        }
    }
    
    // Helper methods (DRY principle)
    private String buildInsertSql() {
        return "INSERT INTO " + DatabaseConstants.TABLE_PETS + 
               " (" + DatabaseConstants.COLUMN_NAME + ", " + DatabaseConstants.COLUMN_SPECIES + ", " +
               DatabaseConstants.COLUMN_BREED + ", " + DatabaseConstants.COLUMN_AGE + ", " +
               DatabaseConstants.COLUMN_WEIGHT + ", " + DatabaseConstants.COLUMN_COLOR + ", " +
               DatabaseConstants.COLUMN_GENDER + ", " + DatabaseConstants.COLUMN_OWNER_ID + ", " +
               DatabaseConstants.COLUMN_HEALTH_STATUS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }
    
    private String buildUpdateSql() {
        return "UPDATE " + DatabaseConstants.TABLE_PETS + " SET " +
               DatabaseConstants.COLUMN_NAME + " = ?, " + DatabaseConstants.COLUMN_SPECIES + " = ?, " +
               DatabaseConstants.COLUMN_BREED + " = ?, " + DatabaseConstants.COLUMN_AGE + " = ?, " +
               DatabaseConstants.COLUMN_WEIGHT + " = ?, " + DatabaseConstants.COLUMN_COLOR + " = ?, " +
               DatabaseConstants.COLUMN_GENDER + " = ?, " + DatabaseConstants.COLUMN_HEALTH_STATUS + " = ?, " +
               DatabaseConstants.COLUMN_OWNER_ID + " = ? WHERE " + DatabaseConstants.COLUMN_PET_ID + " = ?";
    }
    
    private void setPetParameters(PreparedStatement ps, Pet pet) throws SQLException {
        ps.setString(1, pet.getName());
        ps.setString(2, pet.getSpecies());
        ps.setString(3, pet.getBreed());
        ps.setObject(4, pet.getAge());
        ps.setObject(5, pet.getWeight());
        ps.setString(6, pet.getColor());
        ps.setString(7, pet.getGenderDisplayName());
        ps.setObject(8, pet.getOwnerId());
        ps.setString(9, pet.getHealthStatusDisplayName());
    }
}
