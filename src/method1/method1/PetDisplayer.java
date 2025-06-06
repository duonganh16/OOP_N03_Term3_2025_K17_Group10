package method1;

import methods.Pet;

public class PetDisplayer {
    public static <Owner> void printPetDetails(Pet pet) {
        System.out.println("===== Thông Tin Thú Cưng =====");
        System.out.println("ID: " + pet.getTen());
        System.out.println("Tên: " + pet.getName());
        System.out.println("Tuổi: " + pet.getName());
        System.out.println("Loại: " + pet.getTen());

        if (pet.getOwner() != null) {
            Owner owner = (Owner) pet.getOwner(); // Không ép kiểu sai
            System.out.println("Chủ sở hữu: " + ((Pet) owner).getName());
        } else {
            System.out.println("Chủ sở hữu: (Chưa xác định)");
        }
    }

    @Override
    public String toString() {
        return "DisplayerTest []";
    }
}
