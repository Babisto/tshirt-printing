package com.tshirtprinting.stockmanagement.entity;

import com.tshirtprinting.stockmanagement.entity.enums.StockTransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "stock_transactions", indexes = {
        @Index(name = "idx_stock_transactions_variant_created", columnList = "variant_id,createdAt"),
        @Index(name = "idx_stock_transactions_type", columnList = "type")
})
public class StockTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockTransactionType type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer balanceAfter;

    @Column(precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Column(length = 120)
    private String reference;

    @Column(length = 1000)
    private String note;

    @Column(nullable = false)
    private String performedBy;
}
