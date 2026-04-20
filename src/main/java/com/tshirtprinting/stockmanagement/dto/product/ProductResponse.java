package com.tshirtprinting.stockmanagement.dto.product;

import com.tshirtprinting.stockmanagement.entity.enums.ProductCategory;
import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        ProductCategory category,
        String description,
        Boolean active,
        List<ProductVariantResponse> variants,
        BigDecimal totalStockValue
) {
}
