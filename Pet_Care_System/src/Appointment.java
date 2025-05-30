public class Appointment {
    private int id; // Thêm id
    private String petName;
    private String ownerName;
    private String date;
    private String time;
    private String service;

    // Constructor có thêm id
    public Appointment(int id, String petName, String ownerName, String date, String time, String service) {
        this.id = id;
        this.petName = petName;
        this.ownerName = ownerName;
        this.date = date;
        this.time = time;
        this.service = service;
    }
    public Appointment(String petName, String ownerName, String date, String time) {
        this.petName = petName;
        this.ownerName = ownerName;
        this.date = date;
        this.time = time;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getPetName() {
        return petName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getService() {
        return service;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void displayInfo(){
        //code thuc thi phan displayInfo
        System.out.println("phan displayInfo");
    }
}
