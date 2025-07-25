package com.example.servingwebcontent.Model;

import java.util.List;

public class Product {
    private int id; // Thêm id
    private String name;
    private String description;
    private List<DichvuService> services;

    // Constructor
    public Product(String name, double price, String description) {
        this.name = name;
        this.description = description;
    }

    // Getter và Setter cho id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter và Setter cho name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter và Setter cho description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter và Setter cho services
    public List<DichvuService> getServices() {
        return services;
    }

    public void setServices(List<DichvuService> services) {
        this.services = services;
    }

    // Tính tổng chi phí các dịch vụ của sản phẩm bằng đệ quy
    public double getTotalPrice() {
        return Recursion.calculateTotalServicePrice(services, services.size() - 1);
    }

    public void displayInfo(){
        System.out.println("phan displayInfo");
    }
}
