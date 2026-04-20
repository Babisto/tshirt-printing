package com.tshirtprinting.stockmanagement.dto.paint;

import java.math.BigDecimal;

public record PaintUsageResponse(
        Long id,
        Long paintId,
        String paintName,
        String paintType,
        String color,
        BigDecimal quantityUsed,
        BigDecimal unitCostAtUsage,
        BigDecimal totalCost
) {
}
