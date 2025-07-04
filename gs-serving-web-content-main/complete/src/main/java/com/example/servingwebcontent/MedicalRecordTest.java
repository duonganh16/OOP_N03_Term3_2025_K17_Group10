package com.example.servingwebcontent;

import com.example.servingwebcontent.Model.MedicalRecord;

public class MedicalRecordTest {

    public static void test() {
        // Test constructor đầy đủ
        MedicalRecord record1 = new MedicalRecord(1, "Milo", "Viêm da", "Bôi thuốc mỡ", "2025-07-01");
        System.out.println("Record1 ID: " + record1.getId());
        System.out.println("Record1 Pet Name: " + record1.getPetName());
        System.out.println("Record1 Diagnosis: " + record1.getDiagnosis());
        System.out.println("Record1 Treatment: " + record1.getTreatment());
        System.out.println("Record1 Date: " + record1.getDate());

        // Test constructor thiếu ID
        MedicalRecord record2 = new MedicalRecord("Luna", "Sốt", "Tiêm thuốc", "2025-07-02");
        record2.setId(2); // Gán id sau
        System.out.println("\nRecord2 ID: " + record2.getId());
        System.out.println("Record2 Pet Name: " + record2.getPetName());
        System.out.println("Record2 Diagnosis: " + record2.getDiagnosis());
        System.out.println("Record2 Treatment: " + record2.getTreatment());
        System.out.println("Record2 Date: " + record2.getDate());

        // Test setters
        MedicalRecord record3 = new MedicalRecord("", "", "", "");
        record3.setId(3);
        record3.setPetName("Tom");
        record3.setDiagnosis("Viêm tai");
        record3.setTreatment("Làm sạch tai");
        record3.setDate("2025-07-03");

        System.out.println("\nRecord3 ID: " + record3.getId());
        System.out.println("Record3 Pet Name: " + record3.getPetName());
        System.out.println("Record3 Diagnosis: " + record3.getDiagnosis());
        System.out.println("Record3 Treatment: " + record3.getTreatment());
        System.out.println("Record3 Date: " + record3.getDate());

        // Test displayInfo()
        System.out.println("\nTest displayInfo:");
        record1.displayInfo();
    }
}
