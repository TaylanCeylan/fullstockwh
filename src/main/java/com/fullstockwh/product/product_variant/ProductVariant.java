package com.fullstockwh.product.product_variant;

import com.fullstockwh.product.product_variant.enums.Color;
import com.fullstockwh.product.product_variant.enums.Size;
import com.fullstockwh.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_variant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    private Size size;

    @Min(0)
    private Double price;

    @Min(0)
    private Integer stockQuantity;

    //logistics
    private Double weight; // kg
    private Double width;  // cm
    private Double height; // cm
    private Double length; // cm

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
