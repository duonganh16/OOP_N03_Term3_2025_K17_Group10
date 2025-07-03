package com.example.servingwebcontent;

import java.util.ArrayList;
import java.util.List;

import com.example.servingwebcontent.Model.DichvuService;
import com.example.servingwebcontent.Model.Product;

public class ProductTest {

    public static void test() {
        // Tạo các dịch vụ
        DichvuService service1 = new DichvuService(1, "Tắm rửa", 100.0);
        DichvuService service2 = new DichvuService(2, "Cắt tỉa lông", 80.0);
        DichvuService service3 = new DichvuService(3, "Vệ sinh tai", 40.0);

        // Tạo danh sách dịch vụ
        List<DichvuService> serviceList = new ArrayList<>();
        serviceList.add(service1);
        serviceList.add(service2);
        serviceList.add(service3);

        // Tạo sản phẩm và gán dịch vụ
        Product product = new Product("Gói chăm sóc VIP", 0, "Gồm các dịch vụ cao cấp cho thú cưng");
        product.setId(101);
        product.setServices(serviceList);

        // In thông tin sản phẩm
        System.out.println("=== Thông tin sản phẩm ===");
        System.out.println("ID: " + product.getId());
        System.out.println("Tên: " + product.getName());
        System.out.println("Mô tả: " + product.getDescription());

        System.out.println("\n=== Danh sách dịch vụ ===");
        for (DichvuService s : product.getServices()) {
            System.out.println("- " + s.getServiceName() + " | Giá: " + s.getPrice());
        }

        // Tính tổng giá
        System.out.println("\nTổng giá tất cả dịch vụ: " + product.getTotalPrice());

        // Gọi displayInfo
        System.out.println("\nTest displayInfo:");
        product.displayInfo();
    }
}

