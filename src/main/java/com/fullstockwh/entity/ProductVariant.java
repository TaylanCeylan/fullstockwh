package com.fullstockwh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_variant", schema = "fullstockwh")
public class ProductVariant extends BaseEntity
{
    @Column(unique = true, nullable = false)
    private String sku;

    @Version
    private Long version;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION CHECK (price >= 0)")
    @Min(0)
    private Double price;

    @Column(name = "stock_quantity", nullable = false, columnDefinition = "INT CHECK (stock_quantity >= 0)")
    @Min(value = 0, message = "Stock amount cannot be negative")
    private Integer stockQuantity;

    // Logistics attributes
    private Double weight;
    private Double width;
    private Double height;
    private Double length;

    @Builder.Default
    @Column(name = "discount_rate", nullable = false, columnDefinition = "INT DEFAULT 0 CHECK (discount_rate >= 0 AND discount_rate <= 100)")
    @Min(0)
    @Max(100)
    private Integer discountRate = 0;

    // Filters for categories (Example: {"Color": "Black", "Size": "L"})
    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> attributes = new HashMap<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    public Double getSalePrice() {
        if (this.discountRate == null || this.discountRate == 0) return this.price;
        return Math.round((this.price * (1 - (this.discountRate / 100.0))) * 100.0) / 100.0;
    }
}
