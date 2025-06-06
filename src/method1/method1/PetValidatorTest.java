package method1;

import methods.Pet;


public class PetValidatorTest {
    public static void main(String[] args) {
        // Test với pet null => phải trả về false
        Pet nullPet = null;
        assert !PetValidator.validatePet(nullPet) : "Test thất bại: pet null vẫn được coi là hợp lệ.";

        // Test với pet hợp lệ => phải trả về true
        Pet validPet = new Pet(1, "Miu", "Mèo", 2);
        assert PetValidator.validatePet(validPet) : "Test thất bại: pet hợp lệ bị coi là không hợp lệ.";

        System.out.println("Tất cả các kiểm thử validatePet() đã thành công!");
    }
}
