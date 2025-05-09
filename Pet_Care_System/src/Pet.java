public class Pet {
    private String name;
    private String species;
    private int age;

    public Pet(String name, String species, int age) {
        this.name = name;
        this.species = species;
        this.age = age;
    }

    public void showInfo() {
        System.out.println("Pet Name: " + name);
        System.out.println("Species: " + species);
        System.out.println("Age: " + age);
    }
}

