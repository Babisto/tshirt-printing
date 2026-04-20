package com.tshirtprinting.stockmanagement.entity;

import com.tshirtprinting.stockmanagement.entity.enums.PaintUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "paints", indexes = {
        @Index(name = "idx_paints_type_color", columnList = "paintType,color"),
        @Index(name = "idx_paints_owner_email", columnList = "ownerEmail")
})
public class Paint extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String ownerEmail;

    @Column(nullable = false)
    private String paintType;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantityAvailable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaintUnit unit;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costPerUnit;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lowStockThreshold = BigDecimal.TEN;

    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;
}
