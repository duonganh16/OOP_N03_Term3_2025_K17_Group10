package com.example.servingwebcontent.database;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;



public class aivenConnection {
    /*
     * HƯỚNG DẪN CHO NGƯỜI DÙNG TIẾP THEO:
     *
     * 1. Thay thế CONNECTION_URL bên dưới bằng connection string MySQL của bạn
     * 2. Thay thế username và password trong method aivenConn()
     * 3. Đảm bảo database của bạn đã có các bảng cần thiết
     *
     * VÍ DỤ THAY THẾ:
     * private static final String CONNECTION_URL =
     *     "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED";
     *
     * Trong method aivenConn():
     * conn = DriverManager.getConnection(CONNECTION_URL, "YOUR_USERNAME", "YOUR_PASSWORD");
     */

    // Connection string sử dụng Connection 2 - THAY ĐỔI THÔNG TIN NÀY
    private static final String CONNECTION_URL =
        "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED";

    // Test method để kiểm tra kết nối
    public static void main(String[] args) {
        System.out.println("=== KIỂM TRA KẾT NỐI DATABASE (Connection 2) ===");
        aivenConnection test = new aivenConnection();
        test.testConnection();
        test.initializeDatabase();
    }

    public void testConnection() {
        Connection conn = null;
        try {
            System.out.println("1. 📦 Đang tải MySQL Driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("   ✅ MySQL Driver loaded successfully!");

            System.out.println("2. 🔗 Đang kết nối đến Aiven MySQL (Connection 2)...");
            conn = DriverManager.getConnection(CONNECTION_URL, "YOUR_USERNAME", "YOUR_PASSWORD");
            System.out.println("   ✅ Kết nối database thành công!");

            // Test basic query
            System.out.println("3. 🧪 Đang test truy vấn cơ bản...");
            Statement sta = conn.createStatement();
            ResultSet testResult = sta.executeQuery("SELECT 1 as test_value, NOW() as current_time");

            if (testResult.next()) {
                System.out.println("   ✅ Test query thành công!");
                System.out.println("   📊 Test value: " + testResult.getInt("test_value"));
                System.out.println("   ⏰ Server time: " + testResult.getTimestamp("current_time"));
            }

            // Check existing tables
            System.out.println("4. 📋 Kiểm tra các bảng hiện có:");
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            boolean hasTable = false;
            while (tables.next()) {
                hasTable = true;
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("   📋 " + tableName);
            }

            if (!hasTable) {
                System.out.println("   ⚠️  Không tìm thấy bảng nào - cần khởi tạo database!");
            }

            testResult.close();
            tables.close();
            sta.close();
            conn.close();
            System.out.println("5. ✅ Test kết nối hoàn thành!");

        } catch (ClassNotFoundException e) {
            System.out.println("   ❌ Lỗi: Không tìm thấy MySQL Driver");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("   ❌ Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("   ❌ Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void aivenConn() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // THAY ĐỔI: Thay "YOUR_USERNAME" và "YOUR_PASSWORD" bằng username và password database của bạn
            conn = DriverManager.getConnection(CONNECTION_URL, "YOUR_USERNAME", "YOUR_PASSWORD");

                  //  YOUR_PASSWORD_COMMENT_REMOVED
            Statement sta = conn.createStatement();
            ResultSet reset = sta.executeQuery("select * from user");
            System.out.println("Display data from database: ");
            while (reset.next()) {
                String userID = reset.getString("userId");
                String username = reset.getString("username");
                String address = reset.getString("address");
                System.out.println(userID + " " + username + " " + address);

            }

            reset.close();
            sta.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error in database connecion");
            System.out.println(e);
            e.printStackTrace();
        }
    }

    // Method khởi tạo database và tables
    public void initializeDatabase() {
        System.out.println("\n=== KHỞI TẠO DATABASE ===");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, "sqluser", "password");
            Statement stmt = conn.createStatement();

            // 1. Tạo bảng owners (chủ thú cưng)
            System.out.println("1. 🏗️  Tạo bảng owners...");
            String createOwnersTable = """
                CREATE TABLE IF NOT EXISTS owners (
                    owner_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE,
                    phone VARCHAR(20),
                    address TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
                """;
            stmt.executeUpdate(createOwnersTable);
            System.out.println("   ✅ Bảng owners đã được tạo!");

            // 2. Tạo bảng pets (thú cưng)
            System.out.println("2. 🏗️  Tạo bảng pets...");
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
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (owner_id) REFERENCES owners(owner_id) ON DELETE CASCADE
                )
                """;
            stmt.executeUpdate(createPetsTable);
            System.out.println("   ✅ Bảng pets đã được tạo!");

            stmt.close();
            conn.close();
            System.out.println("🎉 Khởi tạo database hoàn thành!");

        } catch (Exception e) {
            System.out.println("❌ Lỗi khi khởi tạo database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
