package com.tshirtprinting.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaintUsageReportItemResponse(
        LocalDateTime date,
        Long printJobId,
        String paintName,
        String paintType,
        String color,
        BigDecimal quantityUsed,
        BigDecimal unitCost,
        BigDecimal totalCost
) {
}
