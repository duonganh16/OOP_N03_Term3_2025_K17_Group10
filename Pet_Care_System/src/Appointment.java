public class Appointment {
    private String petName;
    private String ownerName;
    private String date;
    private String time;
    private String service;

    public Appointment(String petName, String ownerName, String date, String time, String service) {
        this.petName = petName;
        this.ownerName = ownerName;
        this.date = date;
        this.time = time;
        this.service = service;
    }

    // Getters
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
}
