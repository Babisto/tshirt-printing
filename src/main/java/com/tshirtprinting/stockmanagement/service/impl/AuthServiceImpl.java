package com.tshirtprinting.stockmanagement.service.impl;

import com.tshirtprinting.stockmanagement.dto.auth.AuthRequest;
import com.tshirtprinting.stockmanagement.dto.auth.AuthResponse;
import com.tshirtprinting.stockmanagement.dto.auth.RegisterRequest;
import com.tshirtprinting.stockmanagement.entity.AppUser;
import com.tshirtprinting.stockmanagement.entity.enums.UserRole;
import com.tshirtprinting.stockmanagement.exception.BusinessException;
import com.tshirtprinting.stockmanagement.repository.AppUserRepository;
import com.tshirtprinting.stockmanagement.security.JwtService;
import com.tshirtprinting.stockmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        var appUser = appUserRepository.findByEmailAndDeletedFalse(request.email()).orElseThrow();
        String token = jwtService.generateToken(userDetails);
        log.info("User {} authenticated successfully", request.email());
        return new AuthResponse(token, "Bearer", appUser.getEmail(), appUser.getRole().name());
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByEmailAndDeletedFalse(request.email())) {
            throw new BusinessException("An account with that email already exists");
        }

        AppUser user = new AppUser();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.ROLE_ADMIN);
        appUserRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);
        log.info("User {} registered successfully", request.email());
        return new AuthResponse(token, "Bearer", user.getEmail(), user.getRole().name());
    }
}
