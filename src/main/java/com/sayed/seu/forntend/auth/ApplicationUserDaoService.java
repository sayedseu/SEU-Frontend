package com.sayed.seu.forntend.auth;


import com.sayed.seu.forntend.model.ApplicationUserDetails;
import com.sayed.seu.forntend.service.ApplicationUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ApplicationUserDaoService implements ApplicationUserDao {

    private final ApplicationUserDetailsService service;
    private final PasswordEncoder passwordEncoder;

    public ApplicationUserDaoService(ApplicationUserDetailsService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return Optional.ofNullable(getApplicationUser(username));
    }

    private ApplicationUser getApplicationUser(String username) {
        Optional<ApplicationUserDetails> userDetailsOptional = service.retrieve(username);
        if (!userDetailsOptional.isPresent()) return null;
        ApplicationUserDetails userDetails = userDetailsOptional.get();
        return new ApplicationUser(
                userDetails.getUsername(),
                passwordEncoder.encode(userDetails.getPassword()),
                userDetails.getRole().getGrantedAuthorities(),
                userDetails.isAccountNonExpired(),
                userDetails.isAccountNonLocked(),
                userDetails.isCredentialsNonExpired(),
                userDetails.isEnabled()
        );
    }
}
