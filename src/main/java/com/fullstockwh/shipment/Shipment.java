package com.fullstockwh.shipment;

import com.fullstockwh.order.Order;
import com.fullstockwh.shipment.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shipment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    private String carrierName;

    private Double originLat;
    private Double originLon;

    private Double destinationLat;
    private Double destinationLon;

    private Double distanceKm;
    private BigDecimal shippingFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime shippedAt;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime deliveredAt;
    private Double carbonFootprintKg;
}