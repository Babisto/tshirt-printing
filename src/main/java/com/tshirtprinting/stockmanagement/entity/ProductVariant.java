package com.tshirtprinting.stockmanagement.entity;

import com.tshirtprinting.stockmanagement.entity.enums.TShirtSize;
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
@Table(name = "product_variants", indexes = {
        @Index(name = "idx_product_variants_product_size_color", columnList = "product_id,size,color"),
        @Index(name = "idx_product_variants_barcode", columnList = "barcode")
})
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TShirtSize size;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Integer quantityInStock = 0;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal retailPrice;

    @Column
    private String barcode;

    @Column(nullable = false)
    private Integer lowStockThreshold = 10;
}
