package com.example.servingwebcontent;

import com.example.servingwebcontent.Model.Owner;

public class OwnerTest {
    public static void test() {
        // Test constructor đầy đủ
        Owner owner1 = new Owner(1, "Nguyễn Văn A", "123 Đường ABC", "0123456789", "a@example.com");
        System.out.println("Owner1 ID: " + owner1.getId());
        System.out.println("Owner1 Name: " + owner1.getName());
        System.out.println("Owner1 Address: " + owner1.getAddress());
        System.out.println("Owner1 Phone: " + owner1.getPhoneNumber());
        System.out.println("Owner1 Email: " + owner1.getEmail());

        // Test constructor rút gọn
        Owner owner2 = new Owner("Trần Thị B", "0987654321");
        owner2.setId(2);
        owner2.setAddress("456 Đường XYZ");
        owner2.setEmail("b@example.com");

        System.out.println("\nOwner2 ID: " + owner2.getId());
        System.out.println("Owner2 Name: " + owner2.getName());
        System.out.println("Owner2 Address: " + owner2.getAddress());
        System.out.println("Owner2 Phone: " + owner2.getPhoneNumber());
        System.out.println("Owner2 Email: " + owner2.getEmail());

        // Test showInfo
        System.out.println("\nTest showInfo:");
        owner1.showInfo();
    }
}
