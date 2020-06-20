package com.sayed.seu.forntend.auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public enum ApplicationUserRole {
    CHAIRMAN,
    COORDINATOR,
    FACULTY;

    ApplicationUserRole() {
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> role = new HashSet<>();
        role.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return role;
    }
}
