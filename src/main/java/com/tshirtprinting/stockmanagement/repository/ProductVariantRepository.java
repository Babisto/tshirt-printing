package com.tshirtprinting.stockmanagement.repository;

import com.tshirtprinting.stockmanagement.entity.ProductVariant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findByIdAndDeletedFalse(Long id);

    @Query("""
            select pv from ProductVariant pv
            join fetch pv.product p
            where pv.id = :id and pv.deleted = false and p.deleted = false and p.ownerEmail = :ownerEmail
            """)
    Optional<ProductVariant> findOwnedById(@Param("id") Long id, @Param("ownerEmail") String ownerEmail);

    @Query("""
            select pv from ProductVariant pv
            join fetch pv.product p
            where pv.deleted = false and p.deleted = false and p.ownerEmail = :ownerEmail and pv.quantityInStock <= pv.lowStockThreshold
            """)
    List<ProductVariant> findLowStockVariants(@Param("ownerEmail") String ownerEmail);
}
