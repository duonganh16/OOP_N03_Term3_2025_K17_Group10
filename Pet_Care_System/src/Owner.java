public class Owner {
    private String name;
    private String phoneNumber;

    public Owner(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void showInfo() {
        System.out.println("Owner Name: " + name);
        System.out.println("Phone: " + phoneNumber);
    }
}

