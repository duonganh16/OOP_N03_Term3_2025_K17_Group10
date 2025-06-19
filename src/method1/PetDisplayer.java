package method1;

import methods.Pet;

public class PetDisplayer {
    public static void printPetDetails(Pet pet) {
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
    }
}
