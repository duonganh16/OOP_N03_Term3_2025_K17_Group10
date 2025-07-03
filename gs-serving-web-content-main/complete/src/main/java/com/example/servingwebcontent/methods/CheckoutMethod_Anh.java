package com.example.servingwebcontent.methods;

import java.time.LocalDate;
import java.util.List;

public class CheckoutMethod_Anh {

    public static void hienThiThuCungDuocVeNha(List<Pet> danhSach, LocalDate ngay) {
        boolean coThuCung = false;
        for (Pet pet : danhSach) {
            if (pet.getNgayDuocVeNha() != null && pet.getNgayDuocVeNha().equals(ngay)) {
                System.out.println("Thú cưng: " + pet.getTen() + " đã đủ điều kiện được về nhà hôm nay.");
                coThuCung = true;
            }
        }
        if (!coThuCung) {
            System.out.println("Không có thú cưng nào được về nhà trong ngày này.");
        }
    }
}
