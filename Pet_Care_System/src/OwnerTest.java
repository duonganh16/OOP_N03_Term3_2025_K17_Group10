public class OwnerTest {
    public static void test() {
        Owner o = new Owner(1, "Mai Anh", "Hanoi", "0123456789", "maianh@example.com");

        assert o.getId() == 1 : "ID mismatch";
        assert o.getName().equals("Mai Anh") : "Name mismatch";
        assert o.getAddress().equals("Hanoi") : "Address mismatch";
        assert o.getPhoneNumber().equals("0123456789") : "Phone number mismatch";
        assert o.getEmail().equals("maianh@example.com") : "Email mismatch";

        System.out.println("All Owner tests passed!");
    }
}
