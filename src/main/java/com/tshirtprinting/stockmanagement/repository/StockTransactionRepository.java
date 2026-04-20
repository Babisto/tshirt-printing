package com.tshirtprinting.stockmanagement.repository;

import com.tshirtprinting.stockmanagement.entity.StockTransaction;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    @Query("""
            select st from StockTransaction st
            join fetch st.variant v
            join fetch v.product p
            where st.deleted = false
            and v.deleted = false
            and p.deleted = false
            and p.ownerEmail = :ownerEmail
            and (:variantId is null or v.id = :variantId)
            and st.createdAt >= :fromDate
            and st.createdAt <= :toDate
            """)
    Page<StockTransaction> findHistory(@Param("ownerEmail") String ownerEmail,
                                       @Param("variantId") Long variantId,
                                       @Param("fromDate") LocalDateTime fromDate,
                                       @Param("toDate") LocalDateTime toDate,
                                       Pageable pageable);
}
