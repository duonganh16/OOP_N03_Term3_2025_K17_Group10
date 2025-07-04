package com.example.servingwebcontent.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class StatusMigration {
    
    // THAY ĐỔI: Thay thế bằng thông tin database của bạn
    private static final String CONNECTION_URL = "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED";
    private static final String USERNAME = "YOUR_USERNAME";
    private static final String PASSWORD = "YOUR_PASSWORD";
    
    public static void main(String[] args) {
        System.out.println("STARTING STATUS MIGRATION");
        migrateStatus();
        System.out.println("STATUS MIGRATION COMPLETED");
    }
    
    public static void migrateStatus() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to database
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
            stmt = conn.createStatement();

            // Show current status column definition
            System.out.println("\nCurrent status column definition:");
            ResultSet descRs = stmt.executeQuery("DESCRIBE appointments");
            while (descRs.next()) {
                if ("status".equals(descRs.getString("Field"))) {
                    System.out.println("   Type: " + descRs.getString("Type"));
                    System.out.println("   Default: " + descRs.getString("Default"));
                    break;
                }
            }
            descRs.close();

            // Show current status values
            System.out.println("\nCurrent status values in database:");
            ResultSet statusRs = stmt.executeQuery("SELECT DISTINCT status FROM appointments ORDER BY status");
            while (statusRs.next()) {
                System.out.println("   - " + statusRs.getString("status"));
            }
            statusRs.close();

            // Perform the migration
            System.out.println("\nUpdating status column...");
            String alterSQL = """
                ALTER TABLE appointments 
                MODIFY COLUMN status ENUM('Scheduled', 'Confirmed', 'In Progress', 'Completed', 'Cancelled', 'No Show') 
                DEFAULT 'Scheduled'
                """;
            
            stmt.executeUpdate(alterSQL);
            System.out.println("   Status column updated successfully!");

            // Verify the change
            System.out.println("\nVerifying the change:");
            ResultSet verifyRs = stmt.executeQuery("DESCRIBE appointments");
            while (verifyRs.next()) {
                if ("status".equals(verifyRs.getString("Field"))) {
                    System.out.println("   New Type: " + verifyRs.getString("Type"));
                    System.out.println("   Default: " + verifyRs.getString("Default"));
                    break;
                }
            }
            verifyRs.close();

            // Test that we can now insert/update with 'Confirmed' status
            System.out.println("\nTesting new status values...");
            ResultSet testRs = stmt.executeQuery("""
                SELECT COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_NAME = 'appointments' AND COLUMN_NAME = 'status'
                """);
            if (testRs.next()) {
                String columnType = testRs.getString("COLUMN_TYPE");
                System.out.println("   Column definition: " + columnType);

                if (columnType.contains("Confirmed")) {
                    System.out.println("   'Confirmed' status is now supported!");
                } else {
                    System.out.println("   'Confirmed' status is still missing!");
                }
            }
            testRs.close();
            
        } catch (Exception e) {
            System.out.println("Migration failed: " + e.getMessage());
            e.printStackTrace();

            // Try alternative approach if direct ALTER fails
            try {
                System.out.println("\nTrying alternative migration approach...");

                // Create temporary column
                stmt.executeUpdate("ALTER TABLE appointments ADD COLUMN status_new ENUM('Scheduled', 'Confirmed', 'In Progress', 'Completed', 'Cancelled', 'No Show') DEFAULT 'Scheduled'");

                // Copy data
                stmt.executeUpdate("UPDATE appointments SET status_new = status");

                // Replace old column
                stmt.executeUpdate("ALTER TABLE appointments DROP COLUMN status");
                stmt.executeUpdate("ALTER TABLE appointments CHANGE COLUMN status_new status ENUM('Scheduled', 'Confirmed', 'In Progress', 'Completed', 'Cancelled', 'No Show') DEFAULT 'Scheduled'");

                System.out.println("   Alternative migration completed!");

            } catch (Exception e2) {
                System.out.println("   Alternative migration also failed: " + e2.getMessage());
                e2.printStackTrace();
            }
            
        } finally {
            // Clean up
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("Database connection closed.");
            } catch (Exception e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
