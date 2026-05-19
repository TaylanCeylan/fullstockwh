package com.fullstockwh.shipment.dto;

import com.fullstockwh.shipment.enums.ShipmentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ShipmentRowResponse
{
    private Long orderId;
    private String trackingNumber;
    private String customerFullName;
    private String destinationCity;
    private Double distanceKm;
    private BigDecimal shippingFee;
    private Double carbonFootprintKg;
    private ShipmentStatus status;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
}