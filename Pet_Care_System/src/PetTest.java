public class PetTest {
    public static void test() {
        Pet pet = new Pet(1, "Tom", 5, "Cat");

        if (pet.getId() != 1) {
            throw new AssertionError("Expected id to be 1");
        }
        if (!"Tom".equals(pet.getName())) {
            throw new AssertionError("Expected name to be Tom");
        }
        if (pet.getAge() != 5) {
            throw new AssertionError("Expected age to be 5");
        }
        if (!"Cat".equals(pet.getType())) {
            throw new AssertionError("Expected type to be Cat");
        }

        System.out.println("All Pet tests passed!");
    }
}
