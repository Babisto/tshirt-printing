package com.tshirtprinting.stockmanagement.dto.stock;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockTransactionResponse(
        Long id,
        Long variantId,
        String productName,
        String sku,
        String type,
        Integer quantity,
        Integer balanceAfter,
        BigDecimal unitCost,
        String reference,
        String note,
        String performedBy,
        LocalDateTime createdAt
) {
}
