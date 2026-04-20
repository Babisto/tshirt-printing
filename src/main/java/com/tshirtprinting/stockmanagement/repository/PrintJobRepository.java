package com.tshirtprinting.stockmanagement.repository;

import com.tshirtprinting.stockmanagement.entity.PrintJob;
import com.tshirtprinting.stockmanagement.entity.enums.PrintJobStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrintJobRepository extends JpaRepository<PrintJob, Long> {

    Optional<PrintJob> findByIdAndDeletedFalse(Long id);

    Optional<PrintJob> findByIdAndCreatedByAndDeletedFalse(Long id, String createdBy);

    Page<PrintJob> findByCreatedByAndDeletedFalse(String createdBy, Pageable pageable);

    List<PrintJob> findByCreatedByAndStatusAndDeletedFalse(String createdBy, PrintJobStatus status);

    @Query("""
            select distinct pj from PrintJob pj
            join fetch pj.product p
            left join fetch pj.variant v
            left join fetch pj.paintUsages pu
            left join fetch pu.paint pt
            where pj.deleted = false
            and pj.createdBy = :createdBy
            and pj.createdAt >= :fromDate
            and pj.createdAt <= :toDate
            order by pj.createdAt desc
            """)
    List<PrintJob> findForReport(@Param("createdBy") String createdBy,
                                 @Param("fromDate") LocalDateTime fromDate,
                                 @Param("toDate") LocalDateTime toDate);
}
