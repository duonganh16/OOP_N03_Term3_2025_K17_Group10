import java.util.List;

public class Product {
    private int id; // Thêm id
    private String name;
    private String description;
    private List<Service> services;

    // Constructor
    public Product(int id, String name, String description, List<Service> services) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.services = services;
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
    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    // Tính tổng chi phí các dịch vụ của sản phẩm bằng đệ quy
    public double getTotalPrice() {
        return Recursion.calculateTotalServicePrice(services, services.size() - 1);
    }
}
