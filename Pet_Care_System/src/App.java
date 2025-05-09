public class App {
    public static void main(String[] args) {
        System.out.println("=== Testing Pet ===");
        PetTest.test();

        System.out.println("\n=== Testing Owner ===");
        OwnerTest.test();

        System.out.println("\n=== Testing Service ===");
        ServiceTest.test();

        System.out.println("\n=== Testing Appointment ===");
        AppointmentTest.test();

        System.out.println("\n=== Testing MedicalRecord ===");
        MedicalRecordTest.test();

        System.out.println("\n=== Testing Product ===");
        ProductTest.test();
    }
}

