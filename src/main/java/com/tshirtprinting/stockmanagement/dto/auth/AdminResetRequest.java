package com.tshirtprinting.stockmanagement.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AdminResetRequest(
        @NotBlank String password
) {
}
