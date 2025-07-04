package com.example.servingwebcontent.database;

import java.sql.*;

public class SampleDataGenerator {
    
    // HƯỚNG DẪN CHO NGƯỜI DÙNG TIẾP THEO:
    // Thay thế CONNECTION_URL và thông tin đăng nhập trong các method bên dưới
    // bằng thông tin database MySQL của bạn

    // Connection string sử dụng Connection 2 - THAY ĐỔI THÔNG TIN NÀY
    private static final String CONNECTION_URL =
        "jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?ssl-mode=REQUIRED";
    
    public static void main(String[] args) {
        System.out.println("=== TẠO DỮ LIỆU MẪU BỔ SUNG ===");
        SampleDataGenerator generator = new SampleDataGenerator();
        
        generator.addAppointments();
        generator.addMedicalRecords();
        generator.addProducts();
        generator.verifyAllData();
    }
    
    public void addAppointments() {
        System.out.println("\n📅 THÊM DỮ LIỆU APPOINTMENTS");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, "sqluser", "password");
            
            String insertAppointments = """
                INSERT INTO appointments (pet_id, appointment_date, service_type, description, status, cost, veterinarian, notes) VALUES
                (1, '2025-07-05 09:00:00', 'Khám sức khỏe tổng quát', 'Khám định kỳ hàng năm cho Buddy', 'Scheduled', 300000, 'Dr. Nguyễn Văn Minh', 'Pet khỏe mạnh, cần tiêm phòng'),
                (2, '2025-07-03 14:00:00', 'Tiêm phòng', 'Tiêm phòng 4 bệnh cho Mimi', 'Completed', 250000, 'Dr. Trần Thị Lan', 'Đã tiêm xong, hẹn tái khám sau 1 tháng'),
                (3, '2025-07-06 10:30:00', 'Cắt tỉa lông', 'Cắt tỉa và tắm cho Charlie', 'Scheduled', 150000, 'Groomer Lê Văn Tùng', 'Cần cắt tỉa đặc biệt do lông dài'),
                (4, '2025-07-02 16:00:00', 'Khám bệnh', 'Khám vì Luna bị tiêu chảy', 'Completed', 200000, 'Dr. Phạm Thị Hoa', 'Đã khỏi, cho uống thuốc 3 ngày'),
                (5, '2025-07-01 11:00:00', 'Phẫu thuật nhỏ', 'Cắt móng và làm sạch răng cho Max', 'Cancelled', 500000, 'Dr. Hoàng Văn Nam', 'Hủy do chủ không đến'),
                (6, '2025-07-07 15:30:00', 'Khám da liễu', 'Bella bị ngứa và rụng lông', 'Scheduled', 180000, 'Dr. Nguyễn Thị Mai', 'Cần xét nghiệm da'),
                (7, '2025-06-30 08:00:00', 'Điều trị béo phì', 'Rocky cần giảm cân', 'Completed', 350000, 'Dr. Lê Văn Đức', 'Đã lập kế hoạch ăn kiêng')
                """;
            
            PreparedStatement pstmt = conn.prepareStatement(insertAppointments);
            int result = pstmt.executeUpdate();
            System.out.println("   ✅ Đã thêm " + result + " appointments");
            
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("   ❌ Lỗi khi thêm appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void addMedicalRecords() {
        System.out.println("\n🏥 THÊM DỮ LIỆU MEDICAL RECORDS");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, "sqluser", "password");
            
            String insertMedicalRecords = """
                INSERT INTO medical_records (pet_id, visit_date, diagnosis, treatment, medications, veterinarian, follow_up_date, notes) VALUES
                (1, '2025-06-15', 'Khỏe mạnh', 'Khám tổng quát, tiêm phòng', 'Vaccine 5 in 1', 'Dr. Nguyễn Văn Minh', '2026-06-15', 'Tình trạng tốt, cần tiêm phòng hàng năm'),
                (2, '2025-06-20', 'Viêm tai nhẹ', 'Vệ sinh tai, thuốc nhỏ tai', 'Otitis drops', 'Dr. Trần Thị Lan', '2025-07-20', 'Cần vệ sinh tai thường xuyên'),
                (3, '2025-05-10', 'Rối loạn tiêu hóa', 'Điều chỉnh chế độ ăn', 'Probiotics, Digestive enzymes', 'Dr. Phạm Thị Hoa', '2025-08-10', 'Tránh thức ăn cứng, cho ăn nhiều lần/ngày'),
                (4, '2025-07-02', 'Tiêu chảy cấp', 'Điều trị triệu chứng', 'Metronidazole, Probiotics', 'Dr. Phạm Thị Hoa', '2025-07-09', 'Đã khỏi hoàn toàn'),
                (5, '2025-04-25', 'Khỏe mạnh', 'Khám định kỳ', 'Vitamin tổng hợp', 'Dr. Hoàng Văn Nam', '2025-10-25', 'Tình trạng tốt, duy trì chế độ tập luyện'),
                (6, '2025-06-01', 'Viêm da dị ứng', 'Điều trị kháng viêm', 'Antihistamine, Steroid cream', 'Dr. Nguyễn Thị Mai', '2025-07-15', 'Tránh tiếp xúc với chất gây dị ứng'),
                (7, '2025-06-30', 'Béo phì độ 2', 'Chế độ ăn kiêng, tập luyện', 'Weight management food', 'Dr. Lê Văn Đức', '2025-08-30', 'Cần giảm 3kg trong 2 tháng'),
                (1, '2025-03-10', 'Chấn thương nhẹ ở chân', 'Nghỉ ngơi, thuốc giảm đau', 'Meloxicam', 'Dr. Nguyễn Văn Minh', '2025-03-24', 'Đã hồi phục hoàn toàn'),
                (2, '2025-02-14', 'Khám sức khỏe', 'Tiêm phòng, tẩy giun', 'Deworming tablets', 'Dr. Trần Thị Lan', '2025-08-14', 'Lịch tẩy giun 6 tháng/lần'),
                (3, '2025-01-20', 'Viêm lợi nhẹ', 'Vệ sinh răng miệng', 'Dental cleaning gel', 'Dr. Phạm Thị Hoa', '2025-07-20', 'Cần vệ sinh răng hàng tuần')
                """;
            
            PreparedStatement pstmt = conn.prepareStatement(insertMedicalRecords);
            int result = pstmt.executeUpdate();
            System.out.println("   ✅ Đã thêm " + result + " medical records");
            
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("   ❌ Lỗi khi thêm medical records: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void addProducts() {
        System.out.println("\n🛍️ THÊM DỮ LIỆU PRODUCTS");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, "sqluser", "password");

            // Thêm từng product một để tránh lỗi SQL
            String insertProduct = "INSERT INTO products (name, category, description, price, stock_quantity, supplier) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertProduct);

            // Danh sách products
            String[][] products = {
                {"Royal Canin Adult Dog Food", "Thức ăn", "Thức ăn khô cho chó trưởng thành", "450000", "50", "Royal Canin Vietnam"},
                {"Whiskas Cat Food", "Thức ăn", "Thức ăn ướt cho mèo", "25000", "100", "Mars Petcare"},
                {"Kong Classic Dog Toy", "Đồ chơi", "Đồ chơi cao su bền cho chó", "180000", "30", "Kong Company"},
                {"Cat Scratching Post", "Phụ kiện", "Cột cào móng cho mèo", "350000", "20", "Pet Paradise"},
                {"Frontline Flea Treatment", "Thuốc", "Thuốc trị ve rận cho chó mèo", "120000", "40", "Boehringer Ingelheim"},
                {"Pet Carrier Bag", "Phụ kiện", "Túi vận chuyển thú cưng", "280000", "15", "Sherpa Pet"},
                {"Dog Leash and Collar Set", "Phụ kiện", "Bộ dây dắt và vòng cổ cho chó", "150000", "25", "Ruffwear"},
                {"Cat Litter Box", "Phụ kiện", "Khay vệ sinh cho mèo", "200000", "18", "Petmate"},
                {"Vitamin Supplements", "Thuốc", "Vitamin tổng hợp cho thú cưng", "85000", "60", "VetPlus"},
                {"Dental Chew Treats", "Thức ăn", "Bánh quy làm sạch răng cho chó", "95000", "45", "Greenies"},
                {"Catnip Spray", "Đồ chơi", "Xịt cỏ mèo kích thích vui chơi", "65000", "35", "SmartyKat"},
                {"Pet Shampoo", "Chăm sóc", "Dầu gội chuyên dụng cho thú cưng", "120000", "40", "Earthbath"},
                {"Automatic Water Fountain", "Phụ kiện", "Máy uống nước tự động", "450000", "12", "PetSafe"},
                {"Training Treats", "Thức ăn", "Snack huấn luyện cho chó", "75000", "55", "Zukes"},
                {"Pet Bed Cushion", "Phụ kiện", "Đệm nằm êm ái cho thú cưng", "320000", "22", "Furhaven"}
            };

            int count = 0;
            for (String[] product : products) {
                pstmt.setString(1, product[0]); // name
                pstmt.setString(2, product[1]); // category
                pstmt.setString(3, product[2]); // description
                pstmt.setDouble(4, Double.parseDouble(product[3])); // price
                pstmt.setInt(5, Integer.parseInt(product[4])); // stock_quantity
                pstmt.setString(6, product[5]); // supplier

                pstmt.executeUpdate();
                count++;
            }

            System.out.println("   ✅ Đã thêm " + count + " products");

            pstmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("   ❌ Lỗi khi thêm products: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void verifyAllData() {
        System.out.println("\n📊 KIỂM TRA TẤT CẢ DỮ LIỆU");
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONNECTION_URL, "YOUR_USERNAME", "YOUR_PASSWORD");
            Statement stmt = conn.createStatement();
            
            // Đếm tổng số records
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM owners");
            rs.next();
            System.out.println("   👥 Owners: " + rs.getInt("count") + " records");
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM pets");
            rs.next();
            System.out.println("   🐾 Pets: " + rs.getInt("count") + " records");
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM appointments");
            rs.next();
            System.out.println("   📅 Appointments: " + rs.getInt("count") + " records");
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM medical_records");
            rs.next();
            System.out.println("   🏥 Medical Records: " + rs.getInt("count") + " records");
            
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM products");
            rs.next();
            System.out.println("   🛍️ Products: " + rs.getInt("count") + " records");
            
            // Thống kê appointments theo status
            System.out.println("\n   📈 Appointments theo trạng thái:");
            rs = stmt.executeQuery("SELECT status, COUNT(*) as count FROM appointments GROUP BY status");
            while (rs.next()) {
                System.out.println("      • " + rs.getString("status") + ": " + rs.getInt("count"));
            }
            
            // Thống kê products theo category
            System.out.println("\n   🏷️ Products theo danh mục:");
            rs = stmt.executeQuery("SELECT category, COUNT(*) as count FROM products GROUP BY category");
            while (rs.next()) {
                System.out.println("      • " + rs.getString("category") + ": " + rs.getInt("count"));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("\n🎉 TẤT CẢ DỮ LIỆU MẪU ĐÃ ĐƯỢC TẠO THÀNH CÔNG!");
            
        } catch (Exception e) {
            System.out.println("   ❌ Lỗi khi kiểm tra dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
