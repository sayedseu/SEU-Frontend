package com.sayed.seu.forntend.utils;


import com.sayed.seu.forntend.auth.ApplicationUser;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static ApplicationUser CURRENT_USER;

    public static List<String> getSemesterYear() {
        List<String> years = new ArrayList<>();
        years.add("2019");
        years.add("2020");
        years.add("2021");
        years.add("2022");
        years.add("2023");
        years.add("2024");
        years.add("2025");
        return years;
    }
}
