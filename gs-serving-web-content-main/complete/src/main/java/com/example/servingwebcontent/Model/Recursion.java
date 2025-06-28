package com.example.servingwebcontent.Model;

import java.util.List;

public class Recursion {
    public static double calculateTotalServicePrice(List<DichvuService> services, int i) {
        // Trường hợp dừng (không còn phần tử nào)
        if (i < 0) return 0;

        // Đệ quy: tính tổng từ 0 đến i
        return services.get(i).getPrice() + calculateTotalServicePrice(services, i - 1);
    }
}
