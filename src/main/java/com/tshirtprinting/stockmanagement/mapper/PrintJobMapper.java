package com.tshirtprinting.stockmanagement.mapper;

import com.tshirtprinting.stockmanagement.dto.paint.PaintUsageResponse;
import com.tshirtprinting.stockmanagement.dto.printjob.PrintJobResponse;
import com.tshirtprinting.stockmanagement.entity.PrintJob;
import java.util.List;

public final class PrintJobMapper {

    private PrintJobMapper() {
    }

    public static PrintJobResponse toResponse(PrintJob printJob) {
        List<PaintUsageResponse> paintUsages = printJob.getPaintUsages().stream()
                .filter(usage -> !Boolean.TRUE.equals(usage.getDeleted()))
                .map(PaintMapper::toUsageResponse)
                .toList();
        String variantLabel = printJob.getVariant() == null
                ? null
                : printJob.getVariant().getSize() + " / " + printJob.getVariant().getColor();
        return new PrintJobResponse(
                printJob.getId(),
                printJob.getProduct().getId(),
                printJob.getProduct().getName(),
                printJob.getVariant() != null ? printJob.getVariant().getId() : null,
                variantLabel,
                printJob.getQuantityPrinted(),
                printJob.getStatus().name(),
                printJob.getProductionCost(),
                printJob.getRetailValue(),
                printJob.getRetailValue().subtract(printJob.getProductionCost()),
                printJob.getNotes(),
                printJob.getCreatedBy(),
                printJob.getCreatedAt(),
                paintUsages
        );
    }
}
