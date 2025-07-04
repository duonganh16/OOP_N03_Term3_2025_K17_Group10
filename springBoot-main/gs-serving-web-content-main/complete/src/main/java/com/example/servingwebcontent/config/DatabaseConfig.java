package com.example.servingwebcontent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    
    // HƯỚNG DẪN CHO NGƯỜI DÙNG TIẾP THEO:
    // Thay thế các thông tin database connection bên dưới bằng thông tin của bạn:
    // 1. DB_URL: Thay bằng connection string MySQL của bạn
    // 2. DB_USERNAME: Thay bằng username database của bạn
    // 3. DB_PASSWORD: Thay bằng password database của bạn
    //
    // VÍ DỤ:
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database_name?ssl-mode=REQUIRED";
    // private static final String DB_USERNAME = "your_username";
    // private static final String DB_PASSWORD = "your_password";

    // Connection 2 configuration - THAY ĐỔI THÔNG TIN NÀY
    private static final String DB_URL = "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED";
    private static final String DB_USERNAME = "YOUR_USERNAME";
    private static final String DB_PASSWORD = "YOUR_PASSWORD";
    
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        return dataSource;
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
