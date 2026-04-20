package com.tshirtprinting.stockmanagement.specification;

import com.tshirtprinting.stockmanagement.entity.Product;
import com.tshirtprinting.stockmanagement.entity.enums.ProductCategory;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> activeFilters(String search, ProductCategory category, String ownerEmail) {
        return (root, query, cb) -> {
            var predicate = cb.and(
                    cb.isFalse(root.get("deleted")),
                    cb.equal(root.get("ownerEmail"), ownerEmail)
            );
            if (search != null && !search.isBlank()) {
                var pattern = "%" + search.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("sku")), pattern)
                ));
            }
            if (category != null) {
                predicate = cb.and(predicate, cb.equal(root.get("category"), category));
            }
            return predicate;
        };
    }
}
