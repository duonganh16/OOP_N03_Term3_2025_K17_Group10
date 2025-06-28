package methods;

public class Pet {
    private int id;
    private String name;
    private int age;
    private String type;

    public Pet() {
    }

    public Pet(int id, String name, int age, String type) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.type = type;
    }

    public Pet(String name, String type, int age) {
        this.name = name;
        this.age = age;
        this.type = type;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getType() {
        return type;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Hiển thị thông tin thú cưng
    public void showInfo() {
        System.out.println("Thông tin thú cưng:");
        System.out.println("ID: " + id);
        System.out.println("Tên: " + name);
        System.out.println("Tuổi: " + age);
        System.out.println("Loại: " + type);
    }
}
