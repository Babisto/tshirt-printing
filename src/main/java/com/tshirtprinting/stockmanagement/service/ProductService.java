package com.tshirtprinting.stockmanagement.service;

import com.tshirtprinting.stockmanagement.dto.common.PageResponse;
import com.tshirtprinting.stockmanagement.dto.product.DashboardSummaryResponse;
import com.tshirtprinting.stockmanagement.dto.product.ProductRequest;
import com.tshirtprinting.stockmanagement.dto.product.ProductResponse;
import com.tshirtprinting.stockmanagement.entity.enums.ProductCategory;

public interface ProductService {

    ProductResponse create(ProductRequest request, String ownerEmail);

    ProductResponse update(Long id, ProductRequest request, String ownerEmail);

    ProductResponse getById(Long id, String ownerEmail);

    PageResponse<ProductResponse> getAll(String search, ProductCategory category, int page, int size, String ownerEmail);

    void delete(Long id, String ownerEmail);

    DashboardSummaryResponse getDashboardSummary(String ownerEmail);
}
