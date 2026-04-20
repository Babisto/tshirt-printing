package com.tshirtprinting.stockmanagement.service;

import com.tshirtprinting.stockmanagement.dto.common.PageResponse;
import com.tshirtprinting.stockmanagement.dto.stock.StockAdjustmentRequest;
import com.tshirtprinting.stockmanagement.dto.stock.StockOperationRequest;
import com.tshirtprinting.stockmanagement.dto.stock.StockTransactionResponse;
import java.time.LocalDateTime;

public interface StockService {

    StockTransactionResponse addStock(StockOperationRequest request, String actor);

    StockTransactionResponse removeStock(StockOperationRequest request, String actor);

    StockTransactionResponse adjustStock(StockAdjustmentRequest request, String actor);

    PageResponse<StockTransactionResponse> getHistory(Long variantId, LocalDateTime from, LocalDateTime to, int page, int size, String actor);
}
