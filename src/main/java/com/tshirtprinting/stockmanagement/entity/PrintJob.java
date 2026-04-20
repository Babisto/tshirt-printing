package com.tshirtprinting.stockmanagement.entity;

import com.tshirtprinting.stockmanagement.entity.enums.PrintJobStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "print_jobs")
public class PrintJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @Column(nullable = false)
    private Integer quantityPrinted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrintJobStatus status = PrintJobStatus.PLANNED;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal productionCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal retailValue = BigDecimal.ZERO;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private String createdBy;

    @OneToMany(mappedBy = "printJob", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaintUsage> paintUsages = new ArrayList<>();
}
