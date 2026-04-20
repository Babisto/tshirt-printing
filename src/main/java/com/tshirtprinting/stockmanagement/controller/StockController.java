package com.tshirtprinting.stockmanagement.controller;

import com.tshirtprinting.stockmanagement.dto.common.PageResponse;
import com.tshirtprinting.stockmanagement.dto.stock.StockAdjustmentRequest;
import com.tshirtprinting.stockmanagement.dto.stock.StockOperationRequest;
import com.tshirtprinting.stockmanagement.dto.stock.StockTransactionResponse;
import com.tshirtprinting.stockmanagement.service.StockService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/add")
    public ResponseEntity<StockTransactionResponse> addStock(@Valid @RequestBody StockOperationRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.addStock(request, principal.getName()));
    }

    @PostMapping("/remove")
    public ResponseEntity<StockTransactionResponse> removeStock(@Valid @RequestBody StockOperationRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.removeStock(request, principal.getName()));
    }

    @PutMapping("/adjust")
    public ResponseEntity<StockTransactionResponse> adjustStock(@Valid @RequestBody StockAdjustmentRequest request, Principal principal) {
        return ResponseEntity.ok(stockService.adjustStock(request, principal.getName()));
    }

    @GetMapping("/history")
    public ResponseEntity<PageResponse<StockTransactionResponse>> history(@RequestParam(required = false) Long variantId,
                                                                          @RequestParam(required = false) LocalDateTime from,
                                                                          @RequestParam(required = false) LocalDateTime to,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "20") int size,
                                                                          Principal principal) {
        return ResponseEntity.ok(stockService.getHistory(variantId, from, to, page, size, principal.getName()));
    }
}
