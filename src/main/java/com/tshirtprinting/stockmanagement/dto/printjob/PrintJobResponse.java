package com.tshirtprinting.stockmanagement.dto.printjob;

import com.tshirtprinting.stockmanagement.dto.paint.PaintUsageResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PrintJobResponse(
        Long id,
        Long productId,
        String productName,
        Long variantId,
        String variantLabel,
        Integer quantityPrinted,
        String status,
        BigDecimal productionCost,
        BigDecimal retailValue,
        BigDecimal estimatedProfit,
        String notes,
        String createdBy,
        LocalDateTime createdAt,
        List<PaintUsageResponse> paintUsages
) {
}
