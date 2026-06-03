package com.fullstockwh.stock;

import com.fullstockwh.product.product_variant.ProductVariant;
import com.fullstockwh.stock.enums.StockRequestStatus;
import com.fullstockwh.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private UserEntity requestedBy;

    @Column(nullable = false)
    private Integer requestedQuantity;

    @Column(length = 500)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockRequestStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
}
