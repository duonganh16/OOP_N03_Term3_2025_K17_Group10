import java.util.List;

public class Product {
    private String name;
    private String description;
    private List<Service> services;

    // Constructor
    public Product(String name, String description, List<Service> services) {
        this.name = name;
        this.description = description;
        this.services = services;
    }

    // Getter và Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
