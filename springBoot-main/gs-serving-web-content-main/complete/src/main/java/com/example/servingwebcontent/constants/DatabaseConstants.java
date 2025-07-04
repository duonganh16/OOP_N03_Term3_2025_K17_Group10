package com.example.servingwebcontent.constants;

/**
 * Database constants following Clean Code principles
 * Eliminates magic strings and provides centralized configuration
 */
public final class DatabaseConstants {
    
    // Private constructor to prevent instantiation
    private DatabaseConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    // Database Configuration
    // HƯỚNG DẪN CHO NGƯỜI DÙNG TIẾP THEO:
    // 1. Thay thế DB_URL bằng connection string của database MySQL của bạn
    // 2. Thay thế DB_USERNAME và DB_PASSWORD bằng thông tin đăng nhập của bạn
    // 3. Đảm bảo database đã được tạo và có các bảng cần thiết
    //
    // VÍ DỤ THAY THẾ:
    // public static final String DB_URL = "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED";
    // public static final String DB_USERNAME = "YOUR_USERNAME";
    // public static final String DB_PASSWORD = "YOUR_PASSWORD";

    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED";
    public static final String DB_USERNAME = "YOUR_USERNAME";
    public static final String DB_PASSWORD = "YOUR_PASSWORD";
    
    // Table Names
    public static final String TABLE_OWNERS = "owners";
    public static final String TABLE_PETS = "pets";
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String TABLE_MEDICAL_RECORDS = "medical_records";
    public static final String TABLE_PRODUCTS = "products";
    
    // Common Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    
    // Pet specific columns
    public static final String COLUMN_PET_ID = "pet_id";
    public static final String COLUMN_SPECIES = "species";
    public static final String COLUMN_BREED = "breed";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_OWNER_ID = "owner_id";
    public static final String COLUMN_HEALTH_STATUS = "health_status";
    
    // Owner specific columns
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ADDRESS = "address";
    
    // Appointment specific columns
    public static final String COLUMN_APPOINTMENT_ID = "appointment_id";
    public static final String COLUMN_APPOINTMENT_DATE = "appointment_date";
    public static final String COLUMN_SERVICE_TYPE = "service_type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_COST = "cost";
    public static final String COLUMN_VETERINARIAN = "veterinarian";
    public static final String COLUMN_NOTES = "notes";
    
    // Validation Constants
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MAX_PHONE_LENGTH = 20;
    public static final int MIN_AGE = 0;
    public static final int MAX_AGE = 50;
    public static final double MIN_WEIGHT = 0.1;
    public static final double MAX_WEIGHT = 200.0;
    
    // Business Logic Constants
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "name";
    public static final String DEFAULT_SORT_DIRECTION = "ASC";
}
