package method1;

import java.util.List;
public class ViewMethod_MaiAnh {
    public static void displayPetDetails(Pet pet) {
        if (pet == null) {
            System.out.println("Thông tin thú cưng không có sẵn hoặc không tồn tại.");
            return;
        }
        System.out.println("\n===== THÔNG TIN CHI TIẾT THÚ CƯNG =====");
        System.out.printf("%-15s: %d\n", "ID", pet.getId());
        System.out.printf("%-15s: %s\n", "Tên", pet.getName());
        System.out.printf("%-15s: %s\n", "Loài/Type", pet.getType()); 
        System.out.printf("%-15s: %d tuổi\n", "Tuổi", pet.getAge());
        System.out.println("======================================");
    }

    public static void displayPetListTable(List<Pet> pets) {
        if (pets == null || pets.isEmpty()) {
            System.out.println("Danh sách thú cưng trống. Không có gì để hiển thị.");
            return;
        }

        System.out.println("\n=========== DANH SÁCH TẤT CẢ THÚ CƯNG ===========");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-5s\n", "ID", "Tên", "Loài", "Tuổi");
        System.out.println("---------------------------------------------------------------");

        for (Pet pet : pets) {
            System.out.printf("%-5d | %-20s | %-15s | %-5d\n",
                    pet.getId(),
                    pet.getName(),
                    pet.getType(), // Sử dụng 'Type'
                    pet.getAge());
        }
        System.out.println("---------------------------------------------------------------");
        System.out.println("Tổng số thú cưng: " + pets.size());
        System.out.println("=================================================");
    }
    public static void showMessage(String message) {
        System.out.println("\n--- THÔNG BÁO ---");
        System.out.println(message);
        System.out.println("-----------------");
    }
}