package com.example.servingwebcontent.methods;

import java.time.LocalDate;
import java.util.List;

import com.example.servingwebcontent.method2.VaccinationSchedule;

public class VaccinationToday_Huy {
    private List<Pet> petList;

    public void displayVaccinationToday() {
        LocalDate today = LocalDate.now();
        System.out.println("Danh sách thú cưng cần tiêm phòng hôm nay (" + today + "):");

        try {
            for (Pet pet : petList) {
                for (VaccinationSchedule schedule : pet.getVaccinationSchedules()) {
                    if (schedule.getDate().equals(today)) {
                        System.out.println("Tên thú cưng: " + pet.getName());
                        System.out.println("Loại vắc-xin: " + schedule.getVaccineType());
                        System.out.println("Thời gian hẹn: " + schedule.getTime());
                        System.out.println("x----------------------------");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Đã xảy ra lỗi khi kiểm tra lịch tiêm hôm nay: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Hoàn tất kiểm tra lịch tiêm phòng hôm nay.");
        }
    }
}
