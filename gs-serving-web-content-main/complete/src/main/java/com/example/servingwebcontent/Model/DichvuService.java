package com.example.servingwebcontent.Model;

public class DichvuService {
    private int id; // Thêm id
    String serviceName;
    double price;

    // Constructor có id
    public DichvuService(int id, String serviceName, double price) {
        this.id = id;
        this.serviceName = serviceName;
        this.price = price;
    }
    public DichvuService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    // Getter và Setter cho id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter và Setter cho serviceName
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    // Getter và Setter cho price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public void displayService(){
        //code thuc thi phan displayService
        System.out.println("phan display Service");
    }
}
