package com.tshirtprinting.stockmanagement.service.impl;

import com.tshirtprinting.stockmanagement.dto.common.PageResponse;
import com.tshirtprinting.stockmanagement.dto.product.CategoryCountResponse;
import com.tshirtprinting.stockmanagement.dto.product.DashboardSummaryResponse;
import com.tshirtprinting.stockmanagement.dto.product.ProductRequest;
import com.tshirtprinting.stockmanagement.dto.product.ProductResponse;
import com.tshirtprinting.stockmanagement.entity.Product;
import com.tshirtprinting.stockmanagement.entity.ProductVariant;
import com.tshirtprinting.stockmanagement.entity.enums.PrintJobStatus;
import com.tshirtprinting.stockmanagement.entity.enums.ProductCategory;
import com.tshirtprinting.stockmanagement.exception.BusinessException;
import com.tshirtprinting.stockmanagement.exception.ResourceNotFoundException;
import com.tshirtprinting.stockmanagement.mapper.PageMapper;
import com.tshirtprinting.stockmanagement.mapper.ProductMapper;
import com.tshirtprinting.stockmanagement.repository.PrintJobRepository;
import com.tshirtprinting.stockmanagement.repository.ProductRepository;
import com.tshirtprinting.stockmanagement.repository.ProductVariantRepository;
import com.tshirtprinting.stockmanagement.service.ProductService;
import com.tshirtprinting.stockmanagement.specification.ProductSpecification;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PrintJobRepository printJobRepository;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request, String ownerEmail) {
        if (productRepository.findBySkuAndOwnerEmailAndDeletedFalse(request.sku(), ownerEmail).isPresent()) {
            throw new BusinessException("Product with SKU already exists");
        }
        Product product = new Product();
        applyRequest(product, request, ownerEmail);
        Product saved = productRepository.save(product);
        log.info("Created product {} ({})", saved.getName(), saved.getSku());
        return ProductMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request, String ownerEmail) {
        Product product = findProduct(id, ownerEmail);
        productRepository.findBySkuAndOwnerEmailAndDeletedFalse(request.sku(), ownerEmail)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Product with SKU already exists");
                });
        product.getVariants().clear();
        applyRequest(product, request, ownerEmail);
        Product saved = productRepository.save(product);
        log.info("Updated product {}", id);
        return ProductMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id, String ownerEmail) {
        return ProductMapper.toResponse(findProduct(id, ownerEmail));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAll(String search, ProductCategory category, int page, int size, String ownerEmail) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var productPage = productRepository.findAll(ProductSpecification.activeFilters(search, category, ownerEmail), pageable)
                .map(ProductMapper::toResponse);
        return PageMapper.from(productPage);
    }

    @Override
    @Transactional
    public void delete(Long id, String ownerEmail) {
        Product product = findProduct(id, ownerEmail);
        product.setDeleted(Boolean.TRUE);
        product.setActive(Boolean.FALSE);
        product.getVariants().forEach(variant -> variant.setDeleted(Boolean.TRUE));
        productRepository.save(product);
        log.info("Soft deleted product {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary(String ownerEmail) {
        List<Product> products = productRepository.findAll(ProductSpecification.activeFilters(null, null, ownerEmail));
        BigDecimal totalStockValue = products.stream()
                .flatMap(product -> product.getVariants().stream())
                .filter(variant -> !Boolean.TRUE.equals(variant.getDeleted()))
                .map(variant -> variant.getRetailPrice().multiply(BigDecimal.valueOf(variant.getQuantityInStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalStockCost = products.stream()
                .flatMap(product -> product.getVariants().stream())
                .filter(variant -> !Boolean.TRUE.equals(variant.getDeleted()))
                .map(variant -> variant.getUnitCost().multiply(BigDecimal.valueOf(variant.getQuantityInStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal estimatedProfitPotential = totalStockValue.subtract(totalStockCost);
        var completedJobs = printJobRepository.findByCreatedByAndStatusAndDeletedFalse(ownerEmail, PrintJobStatus.COMPLETED);
        BigDecimal completedJobsRevenue = completedJobs.stream()
                .map(printJob -> defaultMoney(printJob.getRetailValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal completedJobsProfit = completedJobs.stream()
                .map(printJob -> defaultMoney(printJob.getRetailValue()).subtract(defaultMoney(printJob.getProductionCost())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        BigDecimal recent30DayProfit = completedJobs.stream()
                .filter(printJob -> printJob.getCreatedAt() != null && !printJob.getCreatedAt().isBefore(last30Days))
                .map(printJob -> defaultMoney(printJob.getRetailValue()).subtract(defaultMoney(printJob.getProductionCost())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageDailyProfit30Days = recent30DayProfit
                .divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        BigDecimal projectedWeeklyProfit = averageDailyProfit30Days
                .multiply(BigDecimal.valueOf(7))
                .setScale(2, RoundingMode.HALF_UP);

        List<CategoryCountResponse> categoryCounts = Arrays.stream(ProductCategory.values())
                .map(category -> ProductMapper.toCategoryCount(
                        category.name(),
                        products.stream()
                                .filter(product -> product.getCategory() == category)
                                .flatMap(product -> product.getVariants().stream())
                                .filter(variant -> !Boolean.TRUE.equals(variant.getDeleted()))
                                .mapToLong(ProductVariant::getQuantityInStock)
                                .sum()
                ))
                .toList();

        var lowStockAlerts = productVariantRepository.findLowStockVariants(ownerEmail).stream()
                .map(ProductMapper::toLowStock)
                .toList();
        return new DashboardSummaryResponse(
                totalStockValue,
                totalStockCost,
                estimatedProfitPotential,
                completedJobsRevenue,
                completedJobsProfit,
                averageDailyProfit30Days,
                projectedWeeklyProfit,
                categoryCounts,
                lowStockAlerts
        );
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void applyRequest(Product product, ProductRequest request, String ownerEmail) {
        product.setSku(request.sku());
        product.setName(request.name());
        product.setOwnerEmail(ownerEmail);
        product.setCategory(request.category());
        product.setDescription(request.description());
        product.setActive(request.active() == null ? Boolean.TRUE : request.active());
        request.variants().forEach(variantRequest -> {
            ProductVariant variant = new ProductVariant();
            variant.setProduct(product);
            variant.setSize(variantRequest.size());
            variant.setColor(variantRequest.color());
            variant.setQuantityInStock(variantRequest.quantityInStock());
            variant.setUnitCost(variantRequest.unitCost());
            variant.setRetailPrice(variantRequest.retailPrice());
            variant.setBarcode(variantRequest.barcode());
            variant.setLowStockThreshold(variantRequest.lowStockThreshold());
            product.getVariants().add(variant);
        });
    }

    private Product findProduct(Long id, String ownerEmail) {
        return productRepository.findByIdAndOwnerEmailAndDeletedFalse(id, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
