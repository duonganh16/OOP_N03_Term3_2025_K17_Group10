package com.example.servingwebcontent.database;

import java.sql.*;

public class DatabaseVerifier {
    
    // HƯỚNG DẪN CHO NGƯỜI DÙNG TIẾP THEO:
    // Thay thế CONNECTION_URL và thông tin đăng nhập bên dưới bằng thông tin database của bạn

    // Connection string sử dụng Connection 2 - THAY ĐỔI THÔNG TIN NÀY
    private static final String CONNECTION_URL =
        "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED";
    
    public static void main(String[] args) {
        System.out.println("=== KIỂM TRA DỮ LIỆU DATABASE ===");
        DatabaseVerifier verifier = new DatabaseVerifier();
        verifier.showAllData();
    }
    
    public void showAllData() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, "YOUR_USERNAME", "YOUR_PASSWORD");
            Statement stmt = conn.createStatement();
            
            System.out.println("\n📋 DANH SÁCH CÁC BẢNG:");
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("   📊 " + tableName);
            }
            
            // Hiển thị dữ liệu owners
            System.out.println("\n👥 BẢNG OWNERS:");
            ResultSet rs = stmt.executeQuery("SELECT * FROM owners ORDER BY owner_id");
            System.out.println("   ID | Tên                | Email                    | Điện thoại    | Địa chỉ");
            System.out.println("   ---|--------------------|--------------------------|--------------|---------");
            
            while (rs.next()) {
                System.out.printf("   %-2d | %-18s | %-24s | %-12s | %s%n",
                    rs.getInt("owner_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address").substring(0, Math.min(30, rs.getString("address").length())) + "..."
                );
            }
            
            // Hiển thị dữ liệu pets
            System.out.println("\n🐾 BẢNG PETS:");
            rs = stmt.executeQuery("""
                SELECT p.pet_id, p.name, p.species, p.breed, p.age, p.weight, 
                       p.color, p.gender, p.health_status, o.name as owner_name
                FROM pets p 
                JOIN owners o ON p.owner_id = o.owner_id 
                ORDER BY p.pet_id
                """);
            
            System.out.println("   ID | Tên      | Loài | Giống           | Tuổi | Cân nặng | Màu    | Giới tính | Tình trạng    | Chủ");
            System.out.println("   ---|----------|-------|------------------|------|----------|--------|-----------|---------------|----------------");
            
            while (rs.next()) {
                System.out.printf("   %-2d | %-8s | %-5s | %-16s | %-4d | %-8.1f | %-6s | %-9s | %-13s | %s%n",
                    rs.getInt("pet_id"),
                    rs.getString("name"),
                    rs.getString("species"),
                    rs.getString("breed"),
                    rs.getInt("age"),
                    rs.getDouble("weight"),
                    rs.getString("color"),
                    rs.getString("gender"),
                    rs.getString("health_status"),
                    rs.getString("owner_name")
                );
            }
            
            // Thống kê
            System.out.println("\n📊 THỐNG KÊ:");
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM owners");
            rs.next();
            System.out.println("   👥 Tổng số chủ thú cưng: " + rs.getInt("count"));
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM pets");
            rs.next();
            System.out.println("   🐾 Tổng số thú cưng: " + rs.getInt("count"));
            
            rs = stmt.executeQuery("SELECT species, COUNT(*) as count FROM pets GROUP BY species");
            System.out.println("   📈 Phân loại theo loài:");
            while (rs.next()) {
                System.out.println("      • " + rs.getString("species") + ": " + rs.getInt("count"));
            }
            
            rs = stmt.executeQuery("SELECT health_status, COUNT(*) as count FROM pets GROUP BY health_status");
            System.out.println("   🏥 Tình trạng sức khỏe:");
            while (rs.next()) {
                System.out.println("      • " + rs.getString("health_status") + ": " + rs.getInt("count"));
            }
            
            // Test CRUD operations
            System.out.println("\n🧪 TEST CRUD OPERATIONS:");
            
            // CREATE - Thêm một pet mới
            System.out.println("   ➕ CREATE: Thêm pet mới...");
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO pets (name, species, breed, age, weight, color, gender, owner_id, health_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            pstmt.setString(1, "TestPet");
            pstmt.setString(2, "Dog");
            pstmt.setString(3, "Test Breed");
            pstmt.setInt(4, 2);
            pstmt.setDouble(5, 10.5);
            pstmt.setString(6, "Brown");
            pstmt.setString(7, "Male");
            pstmt.setInt(8, 1);
            pstmt.setString(9, "Healthy");
            
            int insertResult = pstmt.executeUpdate();
            System.out.println("      ✅ Đã thêm " + insertResult + " record");
            
            // READ - Đọc pet vừa thêm
            System.out.println("   📖 READ: Đọc pet vừa thêm...");
            rs = stmt.executeQuery("SELECT * FROM pets WHERE name = 'TestPet'");
            if (rs.next()) {
                System.out.println("      ✅ Tìm thấy: " + rs.getString("name") + " - " + rs.getString("species"));
            }
            
            // UPDATE - Cập nhật thông tin
            System.out.println("   ✏️  UPDATE: Cập nhật thông tin...");
            pstmt = conn.prepareStatement("UPDATE pets SET age = ? WHERE name = ?");
            pstmt.setInt(1, 3);
            pstmt.setString(2, "TestPet");
            int updateResult = pstmt.executeUpdate();
            System.out.println("      ✅ Đã cập nhật " + updateResult + " record");
            
            // DELETE - Xóa pet test
            System.out.println("   🗑️  DELETE: Xóa pet test...");
            pstmt = conn.prepareStatement("DELETE FROM pets WHERE name = ?");
            pstmt.setString(1, "TestPet");
            int deleteResult = pstmt.executeUpdate();
            System.out.println("      ✅ Đã xóa " + deleteResult + " record");
            
            rs.close();
            stmt.close();
            pstmt.close();
            conn.close();
            
            System.out.println("\n🎉 KIỂM TRA DATABASE HOÀN THÀNH!");
            
        } catch (Exception e) {
            System.out.println("❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
