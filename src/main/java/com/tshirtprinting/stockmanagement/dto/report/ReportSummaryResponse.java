package com.tshirtprinting.stockmanagement.dto.report;

import java.math.BigDecimal;

public record ReportSummaryResponse(
        BigDecimal totalRevenueEstimate,
        BigDecimal totalCost,
        BigDecimal profitMargin
) {
}
