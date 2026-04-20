package com.tshirtprinting.stockmanagement.config;

import com.tshirtprinting.stockmanagement.entity.AppUser;
import com.tshirtprinting.stockmanagement.entity.enums.UserRole;
import com.tshirtprinting.stockmanagement.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin-email}")
    private String adminEmail;

    @Value("${app.bootstrap.admin-password}")
    private String adminPassword;

    @Value("${app.bootstrap.staff-email}")
    private String staffEmail;

    @Value("${app.bootstrap.staff-password}")
    private String staffPassword;

    @Override
    public void run(String... args) {
        createUserIfMissing(adminEmail, adminPassword, "Default Admin", UserRole.ROLE_ADMIN);
        createUserIfMissing(staffEmail, staffPassword, "Default Staff", UserRole.ROLE_STAFF);
    }

    private void createUserIfMissing(String email, String password, String fullName, UserRole role) {
        if (appUserRepository.existsByEmail(email)) {
            return;
        }
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        appUserRepository.save(user);
        log.info("Bootstrapped {} user {}", role, email);
    }
}
