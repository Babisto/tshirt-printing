package com.tshirtprinting.stockmanagement.mapper;

import com.tshirtprinting.stockmanagement.dto.paint.PaintResponse;
import com.tshirtprinting.stockmanagement.dto.paint.PaintUsageResponse;
import com.tshirtprinting.stockmanagement.entity.Paint;
import com.tshirtprinting.stockmanagement.entity.PaintUsage;
import java.math.BigDecimal;

public final class PaintMapper {

    private PaintMapper() {
    }

    public static PaintResponse toResponse(Paint paint) {
        return new PaintResponse(
                paint.getId(),
                paint.getName(),
                paint.getPaintType(),
                paint.getColor(),
                paint.getQuantityAvailable(),
                paint.getUnit(),
                paint.getCostPerUnit(),
                paint.getLowStockThreshold(),
                paint.getActive(),
                paint.getQuantityAvailable().compareTo(paint.getLowStockThreshold()) <= 0
        );
    }

    public static PaintUsageResponse toUsageResponse(PaintUsage usage) {
        BigDecimal totalCost = usage.getQuantityUsed().multiply(usage.getUnitCostAtUsage());
        return new PaintUsageResponse(
                usage.getId(),
                usage.getPaint().getId(),
                usage.getPaint().getName(),
                usage.getPaint().getPaintType(),
                usage.getPaint().getColor(),
                usage.getQuantityUsed(),
                usage.getUnitCostAtUsage(),
                totalCost
        );
    }
}
