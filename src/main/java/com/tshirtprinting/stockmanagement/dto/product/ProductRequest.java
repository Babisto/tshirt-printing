package com.tshirtprinting.stockmanagement.dto.product;

import com.tshirtprinting.stockmanagement.entity.enums.ProductCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @NotNull ProductCategory category,
        String description,
        Boolean active,
        @Valid @NotEmpty List<ProductVariantRequest> variants
) {
}
