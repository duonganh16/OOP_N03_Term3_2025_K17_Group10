 import java.util.List;

public class Recursion {

    // Tính tổng chi phí các dịch vụ chăm sóc thú cưng bằng đệ quy
    public static double calculateTotalServicePrice(List<Service> services, int index) {
        if (index < 0) {
            return 0;
        }
        return services.get(index).getPrice() + calculateTotalServicePrice(services, index - 1);
    }
}
