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

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
    return id;
    }

    public String getName() {
    return name;
    }

    public int getAge(){
    return age;
    }

    public String getType() {
    return type;
    }

    public void showInfo(){
        //code thuc thi phan showInfo
        System.out.println("phan show info");
    }

    public void setType(String newType) {
        throw new UnsupportedOperationException("Unimplemented method 'setType'");
    }
}
