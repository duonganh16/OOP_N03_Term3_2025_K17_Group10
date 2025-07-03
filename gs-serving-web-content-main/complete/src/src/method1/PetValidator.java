package method1;

import methods.Pet;

public class PetValidator {

    /**
     * Phương thức kiểm tra đối tượng Pet có hợp lệ không.
     * @param pet Đối tượng Pet cần kiểm tra.
     * @return true nếu pet không null, ngược lại false và in thông báo lỗi.
     */
    public static boolean validatePet(Pet pet) {
        try {
            if (pet == null) {
                System.out.println("Thông tin thú cưng không có sẵn hoặc không tồn tại.");
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra thú cưng: " + e.getMessage());
            return false;
        } finally {
            System.out.println("Đã thực hiện kiểm tra đối tượng thú cưng.");
        }
    }
}

