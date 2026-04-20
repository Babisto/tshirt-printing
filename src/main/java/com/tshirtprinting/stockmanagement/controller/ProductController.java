package com.tshirtprinting.stockmanagement.controller;

import com.tshirtprinting.stockmanagement.dto.common.PageResponse;
import com.tshirtprinting.stockmanagement.dto.product.DashboardSummaryResponse;
import com.tshirtprinting.stockmanagement.dto.product.ProductRequest;
import com.tshirtprinting.stockmanagement.dto.product.ProductResponse;
import com.tshirtprinting.stockmanagement.entity.enums.ProductCategory;
import com.tshirtprinting.stockmanagement.service.ProductService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request, Principal principal) {
        return ResponseEntity.ok(productService.update(id, request, principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(productService.getById(id, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAll(@RequestParam(required = false) String search,
                                                                @RequestParam(required = false) ProductCategory category,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                Principal principal) {
        return ResponseEntity.ok(productService.getAll(search, category, page, size, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Principal principal) {
        productService.delete(id, principal.getName());
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryResponse> dashboardSummary(Principal principal) {
        return ResponseEntity.ok(productService.getDashboardSummary(principal.getName()));
    }
}
