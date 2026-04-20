package com.tshirtprinting.stockmanagement.service;

import com.tshirtprinting.stockmanagement.dto.auth.AuthRequest;
import com.tshirtprinting.stockmanagement.dto.auth.AuthResponse;
import com.tshirtprinting.stockmanagement.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse register(RegisterRequest request);
}
