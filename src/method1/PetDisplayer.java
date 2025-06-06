public class PetDisplayer {
    public static void printPetDetails(Pet pet) {
        System.out.println("===== Thông Tin Thú Cưng =====");
        System.out.println("ID: " + pet.getId());
        System.out.println("Tên: " + pet.getName());
        System.out.println("Tuổi: " + pet.getAge());
        System.out.println("Loại: " + pet.getType());

        if (pet.getOwner() != null) {
            System.out.println("Chủ sở hữu: " + pet.getOwner().getName());
        } else {
            System.out.println("Chủ sở hữu: (Chưa xác định)");
        }
    }
}
