package com.tshirtprinting.stockmanagement.dto.auth;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        String role
) {
}
