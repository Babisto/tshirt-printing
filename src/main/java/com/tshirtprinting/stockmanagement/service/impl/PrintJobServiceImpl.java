package com.tshirtprinting.stockmanagement.service.impl;

import com.tshirtprinting.stockmanagement.dto.common.PageResponse;
import com.tshirtprinting.stockmanagement.dto.printjob.PrintJobRequest;
import com.tshirtprinting.stockmanagement.dto.printjob.PrintJobResponse;
import com.tshirtprinting.stockmanagement.entity.Paint;
import com.tshirtprinting.stockmanagement.entity.PaintUsage;
import com.tshirtprinting.stockmanagement.entity.PrintJob;
import com.tshirtprinting.stockmanagement.entity.Product;
import com.tshirtprinting.stockmanagement.entity.ProductVariant;
import com.tshirtprinting.stockmanagement.exception.BusinessException;
import com.tshirtprinting.stockmanagement.exception.ResourceNotFoundException;
import com.tshirtprinting.stockmanagement.mapper.PageMapper;
import com.tshirtprinting.stockmanagement.mapper.PrintJobMapper;
import com.tshirtprinting.stockmanagement.repository.PaintRepository;
import com.tshirtprinting.stockmanagement.repository.PrintJobRepository;
import com.tshirtprinting.stockmanagement.repository.ProductRepository;
import com.tshirtprinting.stockmanagement.repository.ProductVariantRepository;
import com.tshirtprinting.stockmanagement.service.PrintJobService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrintJobServiceImpl implements PrintJobService {

    private final PrintJobRepository printJobRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PaintRepository paintRepository;

    @Override
    @Transactional
    public PrintJobResponse create(PrintJobRequest request, String actor) {
        Product product = productRepository.findByIdAndOwnerEmailAndDeletedFalse(request.productId(), actor)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        ProductVariant variant = null;
        if (request.variantId() != null) {
            variant = productVariantRepository.findOwnedById(request.variantId(), actor)
                    .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));
            if (!variant.getProduct().getId().equals(product.getId())) {
                throw new BusinessException("Variant does not belong to selected product");
            }
        }

        PrintJob printJob = new PrintJob();
        printJob.setProduct(product);
        printJob.setVariant(variant);
        printJob.setQuantityPrinted(request.quantityPrinted());
        printJob.setStatus(request.status());
        printJob.setNotes(request.notes());
        printJob.setCreatedBy(actor);

        BigDecimal paintCost = BigDecimal.ZERO;
        for (var usageRequest : request.paintUsages()) {
            Paint paint = paintRepository.findByIdAndOwnerEmailAndDeletedFalse(usageRequest.paintId(), actor)
                    .orElseThrow(() -> new ResourceNotFoundException("Paint not found"));
            if (paint.getQuantityAvailable().compareTo(usageRequest.quantityUsed()) < 0) {
                throw new BusinessException("Insufficient paint stock for " + paint.getName());
            }
            paint.setQuantityAvailable(paint.getQuantityAvailable().subtract(usageRequest.quantityUsed()));

            PaintUsage usage = new PaintUsage();
            usage.setPaint(paint);
            usage.setPrintJob(printJob);
            usage.setQuantityUsed(usageRequest.quantityUsed());
            usage.setUnitCostAtUsage(paint.getCostPerUnit());
            printJob.getPaintUsages().add(usage);
            paintCost = paintCost.add(usageRequest.quantityUsed().multiply(paint.getCostPerUnit()));
        }

        BigDecimal garmentCost = variant != null
                ? variant.getUnitCost().multiply(BigDecimal.valueOf(request.quantityPrinted()))
                : BigDecimal.ZERO;
        BigDecimal retailValue = variant != null
                ? variant.getRetailPrice().multiply(BigDecimal.valueOf(request.quantityPrinted()))
                : BigDecimal.ZERO;

        printJob.setProductionCost(garmentCost.add(paintCost));
        printJob.setRetailValue(retailValue);
        PrintJob saved = printJobRepository.save(printJob);
        log.info("Created print job {} for product {}", saved.getId(), product.getName());
        return PrintJobMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PrintJobResponse getById(Long id, String actor) {
        return PrintJobMapper.toResponse(findPrintJob(id, actor));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PrintJobResponse> getAll(int page, int size, String actor) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = printJobRepository.findByCreatedByAndDeletedFalse(actor, pageable).map(PrintJobMapper::toResponse);
        return PageMapper.from(result);
    }

    private PrintJob findPrintJob(Long id, String actor) {
        return printJobRepository.findByIdAndCreatedByAndDeletedFalse(id, actor)
                .orElseThrow(() -> new ResourceNotFoundException("Print job not found"));
    }
}
