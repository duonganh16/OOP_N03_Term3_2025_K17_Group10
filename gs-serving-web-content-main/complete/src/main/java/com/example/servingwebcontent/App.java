package com.example.servingwebcontent;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args); // Spring Boot khởi động ở đây
    }

    @Override
    public void run(String... args) {
        // Các phần test thực hiện sau khi Spring Boot đã chạy
        System.out.println("=== Testing Pet ===");
        PetTest.test();

        System.out.println("\n=== Testing Owner ===");
        OwnerTest.test();

        System.out.println("\n=== Testing Service ===");
        DichvuServiceTest.test();

        System.out.println("\n=== Testing Appointment ===");
        AppointmentTest.test();

        System.out.println("\n=== Testing MedicalRecord ===");
        MedicalRecordTest.test();

        System.out.println("\n=== Testing Product ===");
        ProductTest.test();
    }
}


