package com.tshirtprinting.stockmanagement.service.impl;

import com.tshirtprinting.stockmanagement.dto.common.PageResponse;
import com.tshirtprinting.stockmanagement.dto.stock.StockAdjustmentRequest;
import com.tshirtprinting.stockmanagement.dto.stock.StockOperationRequest;
import com.tshirtprinting.stockmanagement.dto.stock.StockTransactionResponse;
import com.tshirtprinting.stockmanagement.entity.ProductVariant;
import com.tshirtprinting.stockmanagement.entity.StockTransaction;
import com.tshirtprinting.stockmanagement.entity.enums.StockTransactionType;
import com.tshirtprinting.stockmanagement.exception.BusinessException;
import com.tshirtprinting.stockmanagement.exception.ResourceNotFoundException;
import com.tshirtprinting.stockmanagement.mapper.PageMapper;
import com.tshirtprinting.stockmanagement.mapper.StockMapper;
import com.tshirtprinting.stockmanagement.repository.ProductVariantRepository;
import com.tshirtprinting.stockmanagement.repository.StockTransactionRepository;
import com.tshirtprinting.stockmanagement.service.StockService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductVariantRepository productVariantRepository;
    private final StockTransactionRepository stockTransactionRepository;

    @Override
    @Transactional
    public StockTransactionResponse addStock(StockOperationRequest request, String actor) {
        ProductVariant variant = findVariant(request.variantId(), actor);
        variant.setQuantityInStock(variant.getQuantityInStock() + request.quantity());
        if (request.unitCost() != null) {
            variant.setUnitCost(request.unitCost());
        }
        var saved = createTransaction(variant, request.quantity(), StockTransactionType.ADD, request.reference(), request.note(), actor, request.unitCost());
        log.info("Added {} units to variant {}", request.quantity(), request.variantId());
        return StockMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StockTransactionResponse removeStock(StockOperationRequest request, String actor) {
        ProductVariant variant = findVariant(request.variantId(), actor);
        if (variant.getQuantityInStock() < request.quantity()) {
            throw new BusinessException("Insufficient stock for removal");
        }
        variant.setQuantityInStock(variant.getQuantityInStock() - request.quantity());
        var saved = createTransaction(variant, request.quantity(), StockTransactionType.REMOVE, request.reference(), request.note(), actor, request.unitCost());
        log.info("Removed {} units from variant {}", request.quantity(), request.variantId());
        return StockMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StockTransactionResponse adjustStock(StockAdjustmentRequest request, String actor) {
        ProductVariant variant = findVariant(request.variantId(), actor);
        int previousQuantity = variant.getQuantityInStock();
        variant.setQuantityInStock(request.newQuantity());
        if (request.unitCost() != null) {
            variant.setUnitCost(request.unitCost());
        }
        StockTransaction transaction = new StockTransaction();
        transaction.setVariant(variant);
        transaction.setType(StockTransactionType.ADJUSTMENT);
        transaction.setQuantity(Math.abs(request.newQuantity() - previousQuantity));
        transaction.setBalanceAfter(request.newQuantity());
        transaction.setReference(request.reference());
        transaction.setNote(request.note());
        transaction.setPerformedBy(actor);
        transaction.setUnitCost(request.unitCost());
        log.info("Adjusted stock for variant {} from {} to {}", request.variantId(), previousQuantity, request.newQuantity());
        return StockMapper.toResponse(stockTransactionRepository.save(transaction));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StockTransactionResponse> getHistory(Long variantId, LocalDateTime from, LocalDateTime to, int page, int size, String actor) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        LocalDateTime fromDate = from != null ? from : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime toDate = to != null ? to : LocalDateTime.of(2100, 12, 31, 23, 59, 59);
        Long ownedVariantId = null;
        if (variantId != null) {
            ownedVariantId = findVariant(variantId, actor).getId();
        }
        var history = stockTransactionRepository.findHistory(actor, ownedVariantId, fromDate, toDate, pageable).map(StockMapper::toResponse);
        return PageMapper.from(history);
    }

    private StockTransaction createTransaction(ProductVariant variant,
                                               int quantity,
                                               StockTransactionType type,
                                               String reference,
                                               String note,
                                               String actor,
                                               java.math.BigDecimal unitCost) {
        StockTransaction transaction = new StockTransaction();
        transaction.setVariant(variant);
        transaction.setType(type);
        transaction.setQuantity(quantity);
        transaction.setBalanceAfter(variant.getQuantityInStock());
        transaction.setReference(reference);
        transaction.setNote(note);
        transaction.setPerformedBy(actor);
        transaction.setUnitCost(unitCost != null ? unitCost : variant.getUnitCost());
        return stockTransactionRepository.save(transaction);
    }

    private ProductVariant findVariant(Long id, String actor) {
        return productVariantRepository.findOwnedById(id, actor)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
    }
}
