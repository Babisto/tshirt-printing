package com.tshirtprinting.stockmanagement.dto.paint;

import com.tshirtprinting.stockmanagement.entity.enums.PaintUnit;
import java.math.BigDecimal;

public record PaintResponse(
        Long id,
        String name,
        String paintType,
        String color,
        BigDecimal quantityAvailable,
        PaintUnit unit,
        BigDecimal costPerUnit,
        BigDecimal lowStockThreshold,
        Boolean active,
        boolean lowStock
) {
}
