package com.tshirtprinting.stockmanagement.dto.paint;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaintUsageRequest(
        @NotNull Long paintId,
        @NotNull @DecimalMin("0.01") BigDecimal quantityUsed
) {
}
