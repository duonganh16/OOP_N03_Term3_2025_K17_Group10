package com.example.servingwebcontent.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseMigration {
    
    // Database connection details - THAY ĐỔI THÔNG TIN NÀY
    private static final String CONNECTION_URL = "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?useSSL=true&requireSSL=true&serverTimezone=UTC";
    
    public static void main(String[] args) {
        System.out.println("🔄 STARTING DATABASE MIGRATION");
        migrateAppointmentStatusEnum();
        System.out.println("✅ DATABASE MIGRATION COMPLETED");
    }
    
    public static void migrateAppointmentStatusEnum() {
        System.out.println("\n📅 MIGRATING APPOINTMENT STATUS ENUM");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, "sqluser", "password");
            Statement stmt = conn.createStatement();
            
            System.out.println("   🔍 Checking current status column...");
            
            // First, let's see what values are currently in the status column
            System.out.println("   📊 Current status values in database:");
            var rs = stmt.executeQuery("SELECT DISTINCT status FROM appointments");
            while (rs.next()) {
                System.out.println("      - " + rs.getString("status"));
            }
            rs.close();
            
            // Alter the ENUM to include new values
            System.out.println("   🔧 Altering status column to include new values...");
            String alterStatusEnum = """
                ALTER TABLE appointments 
                MODIFY COLUMN status ENUM('Scheduled', 'Completed', 'Cancelled', 'In Progress', 'No Show') 
                DEFAULT 'Scheduled'
                """;
            
            stmt.executeUpdate(alterStatusEnum);
            System.out.println("   ✅ Status column successfully updated!");
            
            // Verify the change
            System.out.println("   🔍 Verifying the change...");
            var descRs = stmt.executeQuery("DESCRIBE appointments");
            while (descRs.next()) {
                if ("status".equals(descRs.getString("Field"))) {
                    System.out.println("   📋 New status column definition: " + descRs.getString("Type"));
                    break;
                }
            }
            descRs.close();
            
            stmt.close();
            conn.close();
            System.out.println("   🎉 Migration completed successfully!");
            
        } catch (Exception e) {
            System.out.println("   ❌ Error during migration: " + e.getMessage());
            e.printStackTrace();
            
            // If the ALTER fails, it might be because the values already exist
            // Let's try a different approach
            if (e.getMessage().contains("Data truncated") || e.getMessage().contains("Invalid enum value")) {
                System.out.println("   🔄 Trying alternative migration approach...");
                try {
                    conn = DriverManager.getConnection(CONNECTION_URL, "sqluser", "password");
                    Statement stmt = conn.createStatement();
                    
                    // Create a temporary column with the new ENUM
                    System.out.println("   📝 Creating temporary status column...");
                    stmt.executeUpdate("ALTER TABLE appointments ADD COLUMN status_new ENUM('Scheduled', 'Completed', 'Cancelled', 'In Progress', 'No Show') DEFAULT 'Scheduled'");
                    
                    // Copy data from old column to new column
                    System.out.println("   📋 Copying data to new column...");
                    stmt.executeUpdate("UPDATE appointments SET status_new = status");
                    
                    // Drop old column and rename new column
                    System.out.println("   🗑️ Replacing old column...");
                    stmt.executeUpdate("ALTER TABLE appointments DROP COLUMN status");
                    stmt.executeUpdate("ALTER TABLE appointments CHANGE COLUMN status_new status ENUM('Scheduled', 'Completed', 'Cancelled', 'In Progress', 'No Show') DEFAULT 'Scheduled'");
                    
                    stmt.close();
                    conn.close();
                    System.out.println("   ✅ Alternative migration completed!");
                    
                } catch (Exception e2) {
                    System.out.println("   ❌ Alternative migration also failed: " + e2.getMessage());
                    e2.printStackTrace();
                }
            }
        }
    }
}
