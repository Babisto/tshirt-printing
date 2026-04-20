package com.tshirtprinting.stockmanagement.dto.auth;

public record AuthResponse(
        String token,
        String type,
        String email,
        String role
) {
}
