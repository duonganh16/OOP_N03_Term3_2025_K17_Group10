package method1;
import methods.Pet;

public class PetValidator {
     /**
     * Phương thức kiểm tra đối tượng Pet có hợp lệ không.
     * @param pet Đối tượng Pet cần kiểm tra.
     * @return true nếu pet không null, ngược lại false và in thông báo lỗi.
     */
    public static boolean validatePet(Pet pet) {
        if (pet == null) {
            System.out.println("Thông tin thú cưng không có sẵn hoặc không tồn tại.");
            return false;
        }
        return true;
    }
}
