package com.tshirtprinting.stockmanagement.mapper;

import com.tshirtprinting.stockmanagement.dto.product.CategoryCountResponse;
import com.tshirtprinting.stockmanagement.dto.product.LowStockAlertResponse;
import com.tshirtprinting.stockmanagement.dto.product.ProductResponse;
import com.tshirtprinting.stockmanagement.dto.product.ProductVariantResponse;
import com.tshirtprinting.stockmanagement.entity.Product;
import com.tshirtprinting.stockmanagement.entity.ProductVariant;
import java.math.BigDecimal;
import java.util.List;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductResponse toResponse(Product product) {
        List<ProductVariantResponse> variants = product.getVariants().stream()
                .filter(variant -> !Boolean.TRUE.equals(variant.getDeleted()))
                .map(ProductMapper::toVariantResponse)
                .toList();
        BigDecimal totalStockValue = variants.stream()
                .map(variant -> variant.retailPrice().multiply(BigDecimal.valueOf(variant.quantityInStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getCategory(),
                product.getDescription(),
                product.getActive(),
                variants,
                totalStockValue
        );
    }

    public static ProductVariantResponse toVariantResponse(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getId(),
                variant.getSize(),
                variant.getColor(),
                variant.getQuantityInStock(),
                variant.getUnitCost(),
                variant.getRetailPrice(),
                variant.getBarcode(),
                variant.getLowStockThreshold(),
                variant.getQuantityInStock() <= variant.getLowStockThreshold()
        );
    }

    public static LowStockAlertResponse toLowStock(ProductVariant variant) {
        return new LowStockAlertResponse(
                variant.getId(),
                variant.getProduct().getName(),
                variant.getProduct().getSku(),
                variant.getSize().name(),
                variant.getColor(),
                variant.getQuantityInStock(),
                variant.getLowStockThreshold()
        );
    }

    public static CategoryCountResponse toCategoryCount(String category, long quantity) {
        return new CategoryCountResponse(category, quantity);
    }
}
