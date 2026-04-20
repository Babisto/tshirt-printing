package com.tshirtprinting.stockmanagement.dto.product;

public record LowStockAlertResponse(
        Long variantId,
        String productName,
        String sku,
        String size,
        String color,
        Integer quantityInStock,
        Integer threshold
) {
}
