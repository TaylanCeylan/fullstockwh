package com.fullstockwh.shipment.dto;

import com.fullstockwh.shipment.enums.ShipmentStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ShipmentResponse
{
    private Long id;
    private Long orderId;
    private String trackingNumber;
    private String carrierName;
    private Double carbonFootprintKg;

    private Double originLat;
    private Double originLon;
    private Double destinationLat;
    private Double destinationLon;
    private Double distanceKm;

    private BigDecimal shippingFee;
    private ShipmentStatus status;

    private String customerFullName;
    private String customerEmail;

    private String destinationCity;
    private String destinationDistrict;
    private String destinationFullAddress;

    private LocalDateTime createdAt;
    private LocalDateTime shippedAt;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime deliveredAt;

}