package com.example.servingwebcontent.database;

import java.sql.*;

public class DatabaseConnectionTest {
    
    // HƯỚNG DẪN CHO NGƯỜI DÙNG TIẾP THEO:
    // Thay thế các connection strings bên dưới bằng thông tin database MySQL của bạn
    // VÍ DỤ: "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED"

    // Connection strings để test - THAY ĐỔI THÔNG TIN NÀY
    private static final String CONNECTION_1 =
        "jdbc:mysql://YOUR_HOST_1:YOUR_PORT_1/YOUR_DATABASE?ssl-mode=REQUIRED";

    private static final String CONNECTION_2 =
        "jdbc:mysql://YOUR_HOST_2:YOUR_PORT_2/YOUR_DATABASE?ssl-mode=REQUIRED";
    
    public static void main(String[] args) {
        System.out.println("=== KIỂM TRA KẾT NỐI DATABASE AIVEN MYSQL ===\n");
        
        DatabaseConnectionTest test = new DatabaseConnectionTest();
        
        System.out.println("🔍 Test Connection 1:");
        test.testConnection(CONNECTION_1, "Connection 1");
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        System.out.println("🔍 Test Connection 2:");
        test.testConnection(CONNECTION_2, "Connection 2");
    }
    
    public void testConnection(String connectionString, String connectionName) {
        Connection conn = null;
        try {
            System.out.println("1. 📦 Đang tải MySQL Driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("   ✅ MySQL Driver loaded successfully!");
            
            System.out.println("2. 🔗 Đang kết nối đến " + connectionName + "...");
            System.out.println("   📍 URL: " + connectionString.substring(0, 50) + "...");
            
            // THAY ĐỔI: Thay "YOUR_USERNAME" và "YOUR_PASSWORD" bằng username và password database của bạn
            conn = DriverManager.getConnection(connectionString, "YOUR_USERNAME", "YOUR_PASSWORD");
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
            
            // Check database info
            System.out.println("4. 📋 Thông tin database:");
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("   🏷️  Database: " + metaData.getDatabaseProductName());
            System.out.println("   📌 Version: " + metaData.getDatabaseProductVersion());
            System.out.println("   🔗 URL: " + metaData.getURL());
            
            // List tables
            System.out.println("5. 📊 Danh sách bảng trong database:");
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            boolean hasTable = false;
            while (tables.next()) {
                hasTable = true;
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("   📋 " + tableName);
                
                // Show table structure for important tables
                if (tableName.equals("user") || tableName.equals("pets") || tableName.equals("owners")) {
                    showTableStructure(conn, tableName);
                }
            }
            
            if (!hasTable) {
                System.out.println("   ⚠️  Không tìm thấy bảng nào!");
            }
            
            // Test specific queries
            System.out.println("6. 🔍 Test truy vấn dữ liệu:");
            testDataQueries(conn);
            
            // Cleanup
            testResult.close();
            tables.close();
            sta.close();
            conn.close();
            System.out.println("7. ✅ Đóng kết nối thành công!");
            
        } catch (ClassNotFoundException e) {
            System.out.println("   ❌ Lỗi: Không tìm thấy MySQL Driver");
            System.out.println("   💡 Hãy đảm bảo mysql-connector-java đã được thêm vào dependencies");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("   ❌ Lỗi SQL: " + e.getMessage());
            System.out.println("   🔍 SQL State: " + e.getSQLState());
            System.out.println("   🔢 Error Code: " + e.getErrorCode());
            
            if (e.getMessage().contains("Access denied")) {
                System.out.println("   💡 Kiểm tra username/password");
            } else if (e.getMessage().contains("timeout")) {
                System.out.println("   💡 Kiểm tra kết nối mạng và firewall");
            } else if (e.getMessage().contains("Unknown database")) {
                System.out.println("   💡 Kiểm tra tên database");
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showTableStructure(Connection conn, String tableName) {
        try {
            System.out.println("     📝 Cấu trúc bảng " + tableName + ":");
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                int columnSize = columns.getInt("COLUMN_SIZE");
                String nullable = columns.getString("IS_NULLABLE");
                
                System.out.println("       • " + columnName + " (" + dataType + 
                                 (columnSize > 0 ? "(" + columnSize + ")" : "") + 
                                 ", " + (nullable.equals("YES") ? "NULL" : "NOT NULL") + ")");
            }
            columns.close();
        } catch (SQLException e) {
            System.out.println("     ⚠️  Không thể lấy cấu trúc bảng: " + e.getMessage());
        }
    }
    
    private void testDataQueries(Connection conn) {
        try {
            Statement sta = conn.createStatement();
            
            // Test user table
            try {
                ResultSet userResult = sta.executeQuery("SELECT COUNT(*) as user_count FROM user");
                if (userResult.next()) {
                    int count = userResult.getInt("user_count");
                    System.out.println("   👥 Số lượng user: " + count);
                    
                    if (count > 0) {
                        ResultSet sampleUsers = sta.executeQuery("SELECT * FROM user LIMIT 3");
                        System.out.println("   📋 Dữ liệu mẫu từ bảng user:");
                        while (sampleUsers.next()) {
                            System.out.println("     • ID: " + sampleUsers.getString("userId") + 
                                             ", Name: " + sampleUsers.getString("username") + 
                                             ", Address: " + sampleUsers.getString("address"));
                        }
                        sampleUsers.close();
                    }
                }
                userResult.close();
            } catch (SQLException e) {
                System.out.println("   ⚠️  Không thể truy vấn bảng user: " + e.getMessage());
            }
            
            // Test other tables
            String[] testTables = {"pets", "owners", "appointments", "song"};
            for (String table : testTables) {
                try {
                    ResultSet result = sta.executeQuery("SELECT COUNT(*) as count FROM " + table);
                    if (result.next()) {
                        System.out.println("   📊 Số lượng records trong " + table + ": " + result.getInt("count"));
                    }
                    result.close();
                } catch (SQLException e) {
                    System.out.println("   ⚠️  Bảng " + table + " không tồn tại hoặc không thể truy cập");
                }
            }
            
            sta.close();
        } catch (SQLException e) {
            System.out.println("   ❌ Lỗi khi test queries: " + e.getMessage());
        }
    }
}
