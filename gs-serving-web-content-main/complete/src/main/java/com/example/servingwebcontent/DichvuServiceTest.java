package com.example.servingwebcontent;

import com.example.servingwebcontent.Model.DichvuService;

public class DichvuServiceTest {

    public static void test() {
        // Test constructor có đủ id, tên, giá
        DichvuService service1 = new DichvuService(1, "Tắm thú cưng", 100.0);
        System.out.println("Service1 ID: " + service1.getId());
        System.out.println("Service1 Tên: " + service1.getServiceName());
        System.out.println("Service1 Giá: " + service1.getPrice());

        // Test constructor thiếu id
        DichvuService service2 = new DichvuService("Cắt móng", 40.0);
        service2.setId(2); // đặt id sau
        System.out.println("\nService2 ID: " + service2.getId());
        System.out.println("Service2 Tên: " + service2.getServiceName());
        System.out.println("Service2 Giá: " + service2.getPrice());

        // Test setters
        DichvuService service3 = new DichvuService("", 0);
        service3.setId(3);
        service3.setServiceName("Chải lông");
        service3.setPrice(60.0);

        System.out.println("\nService3 ID: " + service3.getId());
        System.out.println("Service3 Tên: " + service3.getServiceName());
        System.out.println("Service3 Giá: " + service3.getPrice());

        // Test displayService()
        System.out.println("\nTest displayService:");
        service1.displayService();
    }
}

