package com.tshirtprinting.stockmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "paint_usages", indexes = {
        @Index(name = "idx_paint_usages_paint_job", columnList = "paint_id,print_job_id")
})
public class PaintUsage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paint_id", nullable = false)
    private Paint paint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "print_job_id", nullable = false)
    private PrintJob printJob;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantityUsed;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCostAtUsage;
}
