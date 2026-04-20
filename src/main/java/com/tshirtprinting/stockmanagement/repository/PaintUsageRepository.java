package com.tshirtprinting.stockmanagement.repository;

import com.tshirtprinting.stockmanagement.entity.PaintUsage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaintUsageRepository extends JpaRepository<PaintUsage, Long> {

    @Query("""
            select pu from PaintUsage pu
            join fetch pu.paint p
            join fetch pu.printJob pj
            where pu.deleted = false
            and p.ownerEmail = :ownerEmail
            and pu.createdAt >= :fromDate
            and pu.createdAt <= :toDate
            order by pu.createdAt desc
            """)
    List<PaintUsage> findForReport(@Param("ownerEmail") String ownerEmail,
                                   @Param("fromDate") LocalDateTime fromDate,
                                   @Param("toDate") LocalDateTime toDate);
}
