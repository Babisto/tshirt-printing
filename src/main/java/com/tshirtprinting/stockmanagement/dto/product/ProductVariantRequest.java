package com.tshirtprinting.stockmanagement.dto.product;

import com.tshirtprinting.stockmanagement.entity.enums.TShirtSize;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductVariantRequest(
        @NotNull TShirtSize size,
        @NotBlank String color,
        @NotNull @Min(0) Integer quantityInStock,
        @NotNull @DecimalMin("0.0") BigDecimal unitCost,
        @NotNull @DecimalMin("0.0") BigDecimal retailPrice,
        String barcode,
        @NotNull @Min(0) Integer lowStockThreshold
) {
}
