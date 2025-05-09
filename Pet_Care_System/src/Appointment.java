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

public void displayInfo() {
System.out.println("Appointment Info:");
System.out.println("Pet: " + petName);
System.out.println("Owner: " + ownerName);
System.out.println("Date: " + date);
System.out.println("Time: " + time);
System.out.println("Service: " + service);
}
}
