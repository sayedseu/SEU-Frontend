package com.sayed.seu.forntend.model;

import com.sayed.seu.forntend.auth.ApplicationUserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ApplicationUserDetails {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private ApplicationUserRole role;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
}
