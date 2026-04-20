package com.tshirtprinting.stockmanagement.dto.product;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal totalStockValue,
        BigDecimal totalStockCost,
        BigDecimal estimatedProfitPotential,
        BigDecimal completedJobsRevenue,
        BigDecimal completedJobsProfit,
        BigDecimal averageDailyProfit30Days,
        BigDecimal projectedWeeklyProfit,
        List<CategoryCountResponse> categoryCounts,
        List<LowStockAlertResponse> lowStockAlerts
) {
}
