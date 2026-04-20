package com.tshirtprinting.stockmanagement.dto.printjob;

import com.tshirtprinting.stockmanagement.dto.paint.PaintUsageRequest;
import com.tshirtprinting.stockmanagement.entity.enums.PrintJobStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PrintJobRequest(
        @NotNull Long productId,
        Long variantId,
        @NotNull @Min(1) Integer quantityPrinted,
        @NotNull PrintJobStatus status,
        String notes,
        @Valid @NotEmpty List<PaintUsageRequest> paintUsages
) {
}
