public class AppointmentTest {
    public static void test() {
        // Test constructor có id
        Appointment a1 = new Appointment(1, "Milo", "John", "2025-06-01", "10:00", "Vaccination");
        assert a1.getId() == 1 : "ID mismatch";
        assert a1.getPetName().equals("Milo") : "Pet name mismatch";
        assert a1.getOwnerName().equals("John") : "Owner name mismatch";
        assert a1.getDate().equals("2025-06-01") : "Date mismatch";
        assert a1.getTime().equals("10:00") : "Time mismatch";
        assert a1.getService().equals("Vaccination") : "Service mismatch";

        // Test constructor không có id
        Appointment a2 = new Appointment("Bella", "Emma", "2025-06-02", "14:00");
        assert a2.getPetName().equals("Bella") : "Pet name mismatch";
        assert a2.getOwnerName().equals("Emma") : "Owner name mismatch";
        assert a2.getDate().equals("2025-06-02") : "Date mismatch";
        assert a2.getTime().equals("14:00") : "Time mismatch";

        System.out.println("All Appointment tests passed!");
    }
}
