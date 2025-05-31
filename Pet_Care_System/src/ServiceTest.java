public class ServiceTest {
    public static void test() {
        // Test constructor có id
        Service s1 = new Service(1, "Grooming", 20.0);
        assert s1.getId() == 1 : "ID mismatch";
        assert s1.serviceName.equals("Grooming") : "Service name mismatch";
        assert s1.price == 20.0 : "Price mismatch";

        // Test constructor không có id
        Service s2 = new Service("Vaccination", 50.0);
        assert s2.serviceName.equals("Vaccination") : "Service name mismatch";
        assert s2.price == 50.0 : "Price mismatch";

        // Test setter và getter cho id
        s2.setId(2);
        assert s2.getId() == 2 : "ID set/get failed";

        System.out.println("All Service tests passed!");
    }
}
