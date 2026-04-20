package com.tshirtprinting.stockmanagement.dto.product;

public record CategoryCountResponse(
        String category,
        long totalQuantity
) {
}
