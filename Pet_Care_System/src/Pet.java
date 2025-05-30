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

    public Pet( String name, String type, int age) {
     
        this.name = name;
        this.age = age;
        this.type = type;
    }

    public int id() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter và Setter cho age
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public void showInfo(){
        //code thuc thi phan showInfo
        System.out.println("phan show info");
    }
}
