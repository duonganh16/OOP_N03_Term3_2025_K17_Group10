package com.example.servingwebcontent;

import com.example.servingwebcontent.Model.Appointment;

public class AppointmentTest {
    public static void test() {
        // Test constructor đầy đủ
        Appointment appt1 = new Appointment(1, "Tom", "Jerry", "2025-07-01", "10:00", "Grooming");
        System.out.println("appt1 ID: " + appt1.getId());
        System.out.println("appt1 Pet: " + appt1.getPetName());
        System.out.println("appt1 Owner: " + appt1.getOwnerName());
        System.out.println("appt1 Date: " + appt1.getDate());
        System.out.println("appt1 Time: " + appt1.getTime());
        System.out.println("appt1 Service: " + appt1.getService());

        // Test constructor thiếu id, service
        Appointment appt2 = new Appointment("Spike", "Tyke", "2025-07-02", "14:00");
        appt2.setId(2);
        appt2.setService("Vaccination");
        System.out.println("\nappt2 ID: " + appt2.getId());
        System.out.println("appt2 Pet: " + appt2.getPetName());
        System.out.println("appt2 Owner: " + appt2.getOwnerName());
        System.out.println("appt2 Date: " + appt2.getDate());
        System.out.println("appt2 Time: " + appt2.getTime());
        System.out.println("appt2 Service: " + appt2.getService());

        // Test displayInfo()
        System.out.println("\nTest displayInfo:");
        appt1.displayInfo();
    }
}
