package com.tshirtprinting.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PrintJobReportItemResponse(
        Long printJobId,
        LocalDateTime date,
        String productName,
        String variant,
        Integer quantityPrinted,
        String status,
        BigDecimal productionCost,
        BigDecimal retailValue,
        BigDecimal estimatedProfit
) {
}
