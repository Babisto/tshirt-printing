package com.tshirtprinting.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockReportItemResponse(
        LocalDateTime date,
        String productName,
        String sku,
        String variant,
        String transactionType,
        Integer quantity,
        Integer balanceAfter,
        BigDecimal retailPrice,
        BigDecimal stockValueAfter,
        String reference,
        String performedBy
) {
}
