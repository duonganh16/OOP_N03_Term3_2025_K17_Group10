public class Service {
    private String serviceName;
    private double price;

    public Service(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public void displayService() {
        System.out.println("Service: " + serviceName + " - Price: $" + price);
    }
}

