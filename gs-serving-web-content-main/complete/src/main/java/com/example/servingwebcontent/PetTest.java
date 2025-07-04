package com.example.servingwebcontent;

import com.example.servingwebcontent.Model.Pet;

public class PetTest {
    public static void test() {
        // Test constructor đầy đủ
        Pet pet1 = new Pet(1, "Milu", 3, "Chó");
        System.out.println("Pet1 ID: " + pet1.getId());
        System.out.println("Pet1 Tên: " + pet1.getName());
        System.out.println("Pet1 Tuổi: " + pet1.getAge());
        System.out.println("Pet1 Loại: " + pet1.getType());

        // Test constructor rút gọn (không có id)
        Pet pet2 = new Pet("Mimi", "Mèo", 2);
        pet2.setId(2); // gán id sau
        System.out.println("\nPet2 ID: " + pet2.getId());
        System.out.println("Pet2 Tên: " + pet2.getName());
        System.out.println("Pet2 Tuổi: " + pet2.getAge());
        System.out.println("Pet2 Loại: " + pet2.getType());

        // Test setter
        Pet pet3 = new Pet();
        pet3.setId(3);
        pet3.setName("Bunny");
        pet3.setAge(1);
        pet3.setType("Thỏ");

        System.out.println("\nPet3 ID: " + pet3.getId());
        System.out.println("Pet3 Tên: " + pet3.getName());
        System.out.println("Pet3 Tuổi: " + pet3.getAge());
        System.out.println("Pet3 Loại: " + pet3.getType());

        // Test showInfo()
        System.out.println("\nTest showInfo:");
        pet1.showInfo();
    }
}
