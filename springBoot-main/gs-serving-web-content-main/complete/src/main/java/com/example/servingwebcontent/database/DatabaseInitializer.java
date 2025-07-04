package com.example.servingwebcontent.database;

import java.sql.*;

public class DatabaseInitializer {
    
    // HƯỚNG DẪN CHO NGƯỜI DÙNG TIẾP THEO:
    // Thay thế CONNECTION_URL bên dưới bằng connection string MySQL của bạn
    // VÍ DỤ: "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED"

    // Connection string sử dụng Connection 2 - THAY ĐỔI THÔNG TIN NÀY
    private static final String CONNECTION_URL =
        "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED";
    
    public static void main(String[] args) {
        System.out.println("=== KHỞI TẠO DATABASE VÀ DỮ LIỆU MẪU ===");
        DatabaseInitializer initializer = new DatabaseInitializer();
        
        // 1. Test kết nối
        if (initializer.testConnection()) {
            // 2. Tạo tables
            initializer.createTables();
            // 3. Thêm dữ liệu mẫu
            initializer.insertSampleData();
            // 4. Verify dữ liệu
            initializer.verifyData();
        }
    }
    
    public boolean testConnection() {
        System.out.println("\n1. 🔍 KIỂM TRA KẾT NỐI DATABASE");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(CONNECTION_URL, "sqluser", "password");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 as test, NOW() as server_time");
            
            if (rs.next()) {
                System.out.println("   ✅ Kết nối thành công!");
                System.out.println("   ⏰ Server time: " + rs.getTimestamp("server_time"));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            return true;
            
        } catch (Exception e) {
            System.out.println("   ❌ Lỗi kết nối: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public void createTables() {
        System.out.println("\n2. 🏗️  TẠO CÁC BẢNG DATABASE");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, "sqluser", "password");
            Statement stmt = conn.createStatement();

            // Check if tables exist and migrate if needed
            System.out.println("   🔍 Kiểm tra và migrate database...");
            migrateAppointmentStatusIfNeeded(stmt);

            // Drop existing tables (để tạo lại từ đầu) - commented out to preserve data
            // System.out.println("   🗑️  Xóa các bảng cũ (nếu có)...");
            // stmt.executeUpdate("DROP TABLE IF EXISTS medical_records");
            // stmt.executeUpdate("DROP TABLE IF EXISTS appointments");
            // stmt.executeUpdate("DROP TABLE IF EXISTS pets");
            // stmt.executeUpdate("DROP TABLE IF EXISTS owners");
            // stmt.executeUpdate("DROP TABLE IF EXISTS products");
            
            // 1. Tạo bảng owners (nếu chưa có)
            System.out.println("   📋 Tạo bảng owners...");
            String createOwnersTable = """
                CREATE TABLE IF NOT EXISTS owners (
                    owner_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE,
                    phone VARCHAR(20),
                    address TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
            stmt.executeUpdate(createOwnersTable);
            
            // 2. Tạo bảng pets (nếu chưa có)
            System.out.println("   🐾 Tạo bảng pets...");
            String createPetsTable = """
                CREATE TABLE IF NOT EXISTS pets (
                    pet_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    species VARCHAR(50) NOT NULL,
                    breed VARCHAR(100),
                    age INT,
                    weight DECIMAL(5,2),
                    color VARCHAR(50),
                    gender ENUM('Male', 'Female', 'Unknown') DEFAULT 'Unknown',
                    owner_id INT,
                    health_status VARCHAR(100) DEFAULT 'Healthy',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (owner_id) REFERENCES owners(owner_id) ON DELETE CASCADE
                )
                """;
            stmt.executeUpdate(createPetsTable);
            
            // 3. Tạo bảng appointments (nếu chưa có)
            System.out.println("   📅 Tạo bảng appointments...");
            String createAppointmentsTable = """
                CREATE TABLE IF NOT EXISTS appointments (
                    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
                    pet_id INT NOT NULL,
                    appointment_date DATETIME NOT NULL,
                    service_type VARCHAR(100) NOT NULL,
                    description TEXT,
                    status ENUM('Scheduled', 'Confirmed', 'In Progress', 'Completed', 'Cancelled', 'No Show') DEFAULT 'Scheduled',
                    cost DECIMAL(10,2),
                    veterinarian VARCHAR(100),
                    notes TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE
                )
                """;
            stmt.executeUpdate(createAppointmentsTable);
            
            // 4. Tạo bảng medical_records
            System.out.println("   🏥 Tạo bảng medical_records...");
            String createMedicalRecordsTable = """
                CREATE TABLE medical_records (
                    record_id INT AUTO_INCREMENT PRIMARY KEY,
                    pet_id INT NOT NULL,
                    visit_date DATE NOT NULL,
                    diagnosis TEXT,
                    treatment TEXT,
                    medications TEXT,
                    veterinarian VARCHAR(100),
                    follow_up_date DATE,
                    notes TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE
                )
                """;
            stmt.executeUpdate(createMedicalRecordsTable);
            
            // 5. Tạo bảng products
            System.out.println("   🛍️  Tạo bảng products...");
            String createProductsTable = """
                CREATE TABLE products (
                    product_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(200) NOT NULL,
                    category VARCHAR(100),
                    description TEXT,
                    price DECIMAL(10,2) NOT NULL,
                    stock_quantity INT DEFAULT 0,
                    supplier VARCHAR(100),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
            stmt.executeUpdate(createProductsTable);
            
            stmt.close();
            conn.close();
            System.out.println("   ✅ Tất cả bảng đã được tạo thành công!");
            
        } catch (Exception e) {
            System.out.println("   ❌ Lỗi khi tạo bảng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void insertSampleData() {
        System.out.println("\n3. 📊 THÊM DỮ LIỆU MẪU");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, "sqluser", "password");
            
            // 1. Thêm owners
            System.out.println("   👥 Thêm dữ liệu owners...");
            String insertOwners = """
                INSERT INTO owners (name, email, phone, address) VALUES
                ('Nguyễn Văn A', 'nguyenvana@email.com', '0123-456-789', '123 Đường ABC, Quận 1, TP.HCM'),
                ('Trần Thị B', 'tranthib@email.com', '0987-654-321', '456 Đường XYZ, Quận 2, TP.HCM'),
                ('Lê Văn C', 'levanc@email.com', '0369-258-147', '789 Đường DEF, Quận 3, TP.HCM'),
                ('Phạm Thị D', 'phamthid@email.com', '0147-258-369', '321 Đường GHI, Quận 4, TP.HCM'),
                ('Hoàng Văn E', 'hoangvane@email.com', '0258-147-369', '654 Đường JKL, Quận 5, TP.HCM')
                """;
            PreparedStatement pstmt = conn.prepareStatement(insertOwners);
            pstmt.executeUpdate();
            pstmt.close();
            
            // 2. Thêm pets
            System.out.println("   🐾 Thêm dữ liệu pets...");
            String insertPets = """
                INSERT INTO pets (name, species, breed, age, weight, color, gender, owner_id, health_status) VALUES
                ('Buddy', 'Dog', 'Golden Retriever', 3, 25.5, 'Golden', 'Male', 1, 'Healthy'),
                ('Mimi', 'Cat', 'Persian', 2, 4.2, 'White', 'Female', 2, 'Healthy'),
                ('Charlie', 'Dog', 'Poodle', 5, 8.7, 'Brown', 'Male', 3, 'Needs Checkup'),
                ('Luna', 'Cat', 'Siamese', 1, 3.1, 'Cream', 'Female', 4, 'Healthy'),
                ('Max', 'Dog', 'German Shepherd', 4, 30.2, 'Black', 'Male', 5, 'Healthy'),
                ('Bella', 'Cat', 'Maine Coon', 3, 5.8, 'Gray', 'Female', 1, 'Healthy'),
                ('Rocky', 'Dog', 'Bulldog', 6, 22.3, 'Brindle', 'Male', 2, 'Overweight')
                """;
            pstmt = conn.prepareStatement(insertPets);
            pstmt.executeUpdate();
            pstmt.close();
            
            System.out.println("   ✅ Dữ liệu mẫu đã được thêm thành công!");
            conn.close();
            
        } catch (Exception e) {
            System.out.println("   ❌ Lỗi khi thêm dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void verifyData() {
        System.out.println("\n4. ✅ KIỂM TRA DỮ LIỆU");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, "sqluser", "password");
            Statement stmt = conn.createStatement();
            
            // Đếm số lượng records
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM owners");
            rs.next();
            System.out.println("   👥 Owners: " + rs.getInt("count") + " records");
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM pets");
            rs.next();
            System.out.println("   🐾 Pets: " + rs.getInt("count") + " records");
            
            // Hiển thị một số dữ liệu mẫu
            System.out.println("\n   📋 Dữ liệu mẫu pets:");
            rs = stmt.executeQuery("""
                SELECT p.name, p.species, p.breed, o.name as owner_name 
                FROM pets p 
                JOIN owners o ON p.owner_id = o.owner_id 
                LIMIT 3
                """);
            
            while (rs.next()) {
                System.out.println("     • " + rs.getString("name") + 
                                 " (" + rs.getString("species") + " - " + rs.getString("breed") + 
                                 ") - Chủ: " + rs.getString("owner_name"));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("\n🎉 KHỞI TẠO DATABASE HOÀN THÀNH!");
            
        } catch (Exception e) {
            System.out.println("   ❌ Lỗi khi kiểm tra dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void migrateAppointmentStatusIfNeeded(Statement stmt) {
        try {
            System.out.println("   🔄 Kiểm tra migration cho appointment status...");

            // Check if appointments table exists
            var rs = stmt.executeQuery("SHOW TABLES LIKE 'appointments'");
            if (!rs.next()) {
                System.out.println("   ℹ️  Bảng appointments chưa tồn tại, sẽ tạo mới");
                rs.close();
                return;
            }
            rs.close();

            // Check current ENUM values
            var descRs = stmt.executeQuery("DESCRIBE appointments");
            String currentEnumDef = "";
            while (descRs.next()) {
                if ("status".equals(descRs.getString("Field"))) {
                    currentEnumDef = descRs.getString("Type");
                    break;
                }
            }
            descRs.close();

            System.out.println("   📋 Current status definition: " + currentEnumDef);

            // Check if we need to migrate
            if (!currentEnumDef.contains("In Progress") || !currentEnumDef.contains("No Show") || !currentEnumDef.contains("Confirmed")) {
                System.out.println("   🔧 Migrating status column to include new values...");

                try {
                    // Try direct ALTER first
                    String alterStatusEnum = """
                        ALTER TABLE appointments
                        MODIFY COLUMN status ENUM('Scheduled', 'Confirmed', 'In Progress', 'Completed', 'Cancelled', 'No Show')
                        DEFAULT 'Scheduled'
                        """;
                    stmt.executeUpdate(alterStatusEnum);
                    System.out.println("   ✅ Status column migration completed!");

                } catch (Exception e) {
                    System.out.println("   ⚠️  Direct ALTER failed, trying alternative approach...");

                    // Alternative approach: add temp column, copy data, replace
                    stmt.executeUpdate("ALTER TABLE appointments ADD COLUMN status_temp ENUM('Scheduled', 'Confirmed', 'In Progress', 'Completed', 'Cancelled', 'No Show') DEFAULT 'Scheduled'");
                    stmt.executeUpdate("UPDATE appointments SET status_temp = status");
                    stmt.executeUpdate("ALTER TABLE appointments DROP COLUMN status");
                    stmt.executeUpdate("ALTER TABLE appointments CHANGE COLUMN status_temp status ENUM('Scheduled', 'Confirmed', 'In Progress', 'Completed', 'Cancelled', 'No Show') DEFAULT 'Scheduled'");
                    System.out.println("   ✅ Alternative migration completed!");
                }
            } else {
                System.out.println("   ✅ Status column already up to date");
            }

        } catch (Exception e) {
            System.out.println("   ⚠️  Migration check failed: " + e.getMessage());
            // Continue anyway - table creation will handle it
        }
    }
}
