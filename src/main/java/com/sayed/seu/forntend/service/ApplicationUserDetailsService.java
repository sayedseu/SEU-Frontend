package com.sayed.seu.forntend.service;

import com.sayed.seu.forntend.auth.ApplicationUserRole;
import com.sayed.seu.forntend.model.ApplicationUserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationUserDetailsService {


    public Optional<ApplicationUserDetails> retrieve(String username) {
        return getUserList().stream()
                .filter(i -> i.getUsername().equals(username))
                .findFirst();
    }

    private List<ApplicationUserDetails> getUserList() {
        List<ApplicationUserDetails> userDetailsList = new ArrayList<>();
        userDetailsList.add(new ApplicationUserDetails(
                "SM",
                "admin",
                "Shahriar Manzoor",
                "sm@seu.edu.bd",
                ApplicationUserRole.CHAIRMAN,
                true,
                true,
                true,
                true
        ));
        userDetailsList.add(new ApplicationUserDetails(
                "RB",
                "admin",
                "Rajon Bardhan",
                "rb@seu.edu.bd",
                ApplicationUserRole.COORDINATOR,
                true,
                true,
                true,
                true
        ));
        userDetailsList.add(new ApplicationUserDetails(
                "MAHB",
                "admin",
                "Mahbub Hasan Mridul",
                "mahb@seu.edu.bd",
                ApplicationUserRole.FACULTY,
                true,
                true,
                true,
                true
        ));
        return userDetailsList;
    }
}
