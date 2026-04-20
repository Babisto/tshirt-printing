package com.tshirtprinting.stockmanagement.dto.paint;

import com.tshirtprinting.stockmanagement.entity.enums.PaintUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaintRequest(
        @NotBlank String name,
        @NotBlank String paintType,
        @NotBlank String color,
        @NotNull @DecimalMin("0.0") BigDecimal quantityAvailable,
        @NotNull PaintUnit unit,
        @NotNull @DecimalMin("0.0") BigDecimal costPerUnit,
        @NotNull @DecimalMin("0.0") BigDecimal lowStockThreshold,
        Boolean active
) {
}
