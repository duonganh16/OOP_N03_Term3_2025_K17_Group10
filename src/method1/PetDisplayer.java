package method1;

import methods.Pet;

public class PetDisplayer {
    public static void printPetDetails(Pet pet) {
        try {
            System.out.println("===== Thông Tin Thú Cưng =====");
            System.out.println("ID: " + pet.getTen());
            System.out.println("Tên: " + pet.getName());
            System.out.println("Tuổi: " + pet.getName());
            System.out.println("Loại: " + pet.getTen());

            if (pet.getOwner() != null) {
                System.out.println("Chủ sở hữu: " + ((Pet) pet.getOwner()).getName());
            } else {
                System.out.println("Chủ sở hữu: (Chưa xác định)");
            }
        } catch (Exception e) {
            System.out.println("Đã xảy ra lỗi khi hiển thị thông tin thú cưng:");
            e.printStackTrace();
        } finally {
            System.out.println("----- Kết thúc hiển thị thông tin -----");
        }
    }
}
