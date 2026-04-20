package com.tshirtprinting.stockmanagement.repository;

import com.tshirtprinting.stockmanagement.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByIdAndDeletedFalse(Long id);

    Optional<Product> findByIdAndOwnerEmailAndDeletedFalse(Long id, String ownerEmail);

    Optional<Product> findBySkuAndDeletedFalse(String sku);

    Optional<Product> findBySkuAndOwnerEmailAndDeletedFalse(String sku, String ownerEmail);

    boolean existsBySku(String sku);
}
