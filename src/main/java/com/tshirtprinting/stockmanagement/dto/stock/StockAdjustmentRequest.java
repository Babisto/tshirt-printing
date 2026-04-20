package com.tshirtprinting.stockmanagement.dto.stock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record StockAdjustmentRequest(
        @NotNull Long variantId,
        @NotNull @Min(0) Integer newQuantity,
        BigDecimal unitCost,
        @NotBlank String reference,
        String note
) {
}
