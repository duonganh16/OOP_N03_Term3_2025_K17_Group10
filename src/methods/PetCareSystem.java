package methods;

import java.time.LocalDate;
import java.util.List;

import method2.VaccinationSchedule;

public class PetCareSystem {
    private List<Pet> petList;

    public void displayVaccinationToday() {
        LocalDate today = LocalDate.now();
        System.out.println("Danh sách thú cưng cần tiêm phòng hôm nay (" + today + "):");
        for (Pet pet : petList) {
            for (VaccinationSchedule schedule : pet.getVaccinationSchedules()) {
                if (schedule.getDate().equals(today)) {
                    System.out.println("Tên thú cưng: " + pet.getName());
                    System.out.println("Loại vắc-xin: " + schedule.getVaccineType());
                    System.out.println("Thời gian hẹn: " + schedule.getTime());
                    System.out.println("---------------------------");
                }
            }
        }
    }
}
