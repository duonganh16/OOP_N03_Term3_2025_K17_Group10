package com.example.servingwebcontent.repository;

import com.example.servingwebcontent.constants.DatabaseConstants;
import com.example.servingwebcontent.exception.DatabaseException;
import com.example.servingwebcontent.model.Owner;
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
 * Owner Repository Implementation following OOP principles
 * Implements BaseRepository interface (Abstraction)
 */
@Repository
public class OwnerRepositoryImpl implements BaseRepository<Owner, Long> {
    
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Owner> ownerRowMapper;
    
    @Autowired
    public OwnerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.ownerRowMapper = new OwnerRowMapper();
    }
    
    private static class OwnerRowMapper implements RowMapper<Owner> {
        @Override
        public Owner mapRow(ResultSet rs, int rowNum) throws SQLException {
            Owner owner = new Owner();
            owner.setOwnerId(rs.getLong(DatabaseConstants.COLUMN_OWNER_ID));
            owner.setName(rs.getString(DatabaseConstants.COLUMN_NAME));
            owner.setEmail(rs.getString(DatabaseConstants.COLUMN_EMAIL));
            owner.setPhone(rs.getString(DatabaseConstants.COLUMN_PHONE));
            owner.setAddress(rs.getString(DatabaseConstants.COLUMN_ADDRESS));
            
            if (rs.getTimestamp(DatabaseConstants.COLUMN_CREATED_AT) != null) {
                owner.setCreatedAt(rs.getTimestamp(DatabaseConstants.COLUMN_CREATED_AT).toLocalDateTime());
            }
            
            return owner;
        }
    }
    
    @Override
    public Owner save(Owner owner) {
        if (owner == null || !owner.isValid()) {
            throw new DatabaseException("Invalid owner data", null);
        }
        
        try {
            String sql = "INSERT INTO " + DatabaseConstants.TABLE_OWNERS + 
                        " (" + DatabaseConstants.COLUMN_NAME + ", " + DatabaseConstants.COLUMN_EMAIL + ", " +
                        DatabaseConstants.COLUMN_PHONE + ", " + DatabaseConstants.COLUMN_ADDRESS + 
                        ") VALUES (?, ?, ?, ?)";
            
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, owner.getName());
                ps.setString(2, owner.getEmail());
                ps.setString(3, owner.getPhone());
                ps.setString(4, owner.getAddress());
                return ps;
            }, keyHolder);
            
            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                owner.setOwnerId(generatedId.longValue());
            }
            
            return owner;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to save owner: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Owner> findAll() {
        try {
            String sql = "SELECT * FROM " + DatabaseConstants.TABLE_OWNERS + " ORDER BY " + DatabaseConstants.COLUMN_NAME;
            return jdbcTemplate.query(sql, ownerRowMapper);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to retrieve all owners: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Owner> findById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        
        try {
            String sql = "SELECT * FROM " + DatabaseConstants.TABLE_OWNERS + 
                        " WHERE " + DatabaseConstants.COLUMN_OWNER_ID + " = ?";
            
            List<Owner> owners = jdbcTemplate.query(sql, ownerRowMapper, id);
            return owners.isEmpty() ? Optional.empty() : Optional.of(owners.get(0));
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to find owner by ID: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Owner update(Owner owner) {
        if (owner == null || owner.getOwnerId() == null || !owner.isValid()) {
            throw new DatabaseException("Invalid owner data for update", null);
        }
        
        try {
            String sql = "UPDATE " + DatabaseConstants.TABLE_OWNERS + " SET " +
                        DatabaseConstants.COLUMN_NAME + " = ?, " + DatabaseConstants.COLUMN_EMAIL + " = ?, " +
                        DatabaseConstants.COLUMN_PHONE + " = ?, " + DatabaseConstants.COLUMN_ADDRESS + " = ? " +
                        "WHERE " + DatabaseConstants.COLUMN_OWNER_ID + " = ?";
            
            int rowsAffected = jdbcTemplate.update(sql, 
                owner.getName(), owner.getEmail(), owner.getPhone(), 
                owner.getAddress(), owner.getOwnerId());
            
            if (rowsAffected == 0) {
                throw new DatabaseException("Owner not found for update", null);
            }
            
            owner.updateTimestamp();
            return owner;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update owner: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        try {
            String sql = "DELETE FROM " + DatabaseConstants.TABLE_OWNERS + 
                        " WHERE " + DatabaseConstants.COLUMN_OWNER_ID + " = ?";
            
            int rowsAffected = jdbcTemplate.update(sql, id);
            return rowsAffected > 0;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete owner: " + e.getMessage(), e);
        }
    }
    
    @Override
    public long count() {
        try {
            String sql = "SELECT COUNT(*) FROM " + DatabaseConstants.TABLE_OWNERS;
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to count owners: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        try {
            String sql = "SELECT COUNT(*) FROM " + DatabaseConstants.TABLE_OWNERS + 
                        " WHERE " + DatabaseConstants.COLUMN_OWNER_ID + " = ?";
            
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
            return count != null && count > 0;
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to check owner existence: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Owner> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            String sql = "SELECT * FROM " + DatabaseConstants.TABLE_OWNERS + 
                        " WHERE " + DatabaseConstants.COLUMN_NAME + " LIKE ? ORDER BY " + DatabaseConstants.COLUMN_NAME;
            
            return jdbcTemplate.query(sql, ownerRowMapper, "%" + name.trim() + "%");
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to search owners by name: " + e.getMessage(), e);
        }
    }
    
    // Additional business-specific methods
    public Optional<Owner> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            String sql = "SELECT * FROM " + DatabaseConstants.TABLE_OWNERS + 
                        " WHERE " + DatabaseConstants.COLUMN_EMAIL + " = ?";
            
            List<Owner> owners = jdbcTemplate.query(sql, ownerRowMapper, email.trim().toLowerCase());
            return owners.isEmpty() ? Optional.empty() : Optional.of(owners.get(0));
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to find owner by email: " + e.getMessage(), e);
        }
    }
    
    public List<Owner> findByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            String sql = "SELECT * FROM " + DatabaseConstants.TABLE_OWNERS + 
                        " WHERE " + DatabaseConstants.COLUMN_PHONE + " LIKE ?";
            
            return jdbcTemplate.query(sql, ownerRowMapper, "%" + phone.trim() + "%");
            
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to find owners by phone: " + e.getMessage(), e);
        }
    }
}
