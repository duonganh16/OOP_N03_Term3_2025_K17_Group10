public class MedicalRecord {
    private int id; // Thêm thuộc tính id
    private String petName;
    private String diagnosis;
    private String treatment;
    private String date;

    // Constructor có thêm id
    public MedicalRecord(int id, String petName, String diagnosis, String treatment, String date) {
        this.id = id;
        this.petName = petName;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.date = date;
    }

   
    public MedicalRecord(String petName, String diagnosis, String treatment, String date) {
     
        this.petName = petName;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getPetName() {
        return petName;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void displayInfo(){
        //code thuc thi phan displayInfo
        System.out.println("phan displayInfo");
    }
}
