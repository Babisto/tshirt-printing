package com.tshirtprinting.stockmanagement.mapper;

import com.tshirtprinting.stockmanagement.dto.stock.StockTransactionResponse;
import com.tshirtprinting.stockmanagement.entity.StockTransaction;

public final class StockMapper {

    private StockMapper() {
    }

    public static StockTransactionResponse toResponse(StockTransaction transaction) {
        return new StockTransactionResponse(
                transaction.getId(),
                transaction.getVariant().getId(),
                transaction.getVariant().getProduct().getName(),
                transaction.getVariant().getProduct().getSku(),
                transaction.getType().name(),
                transaction.getQuantity(),
                transaction.getBalanceAfter(),
                transaction.getUnitCost(),
                transaction.getReference(),
                transaction.getNote(),
                transaction.getPerformedBy(),
                transaction.getCreatedAt()
        );
    }
}
