public class ProductTest {
    public static void test() {
        Product p = new Product("Shampoo", 9.99, "Pet shampoo");

        assert p.getName().equals("Shampoo") : "Name mismatch";
        assert p.getTotalPrice() == 9.99 : "Price mismatch";
        assert p.getDescription().equals("Pet shampoo") : "Description mismatch";

        System.out.println("All Product tests passed!");
    }
}
