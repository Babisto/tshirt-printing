package com.tshirtprinting.stockmanagement.dto.product;

import com.tshirtprinting.stockmanagement.entity.enums.TShirtSize;
import java.math.BigDecimal;

public record ProductVariantResponse(
        Long id,
        TShirtSize size,
        String color,
        Integer quantityInStock,
        BigDecimal unitCost,
        BigDecimal retailPrice,
        String barcode,
        Integer lowStockThreshold,
        boolean lowStock
) {
}
