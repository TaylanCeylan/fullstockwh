package com.fullstockwh.shipment.dto;

import com.fullstockwh.order.enums.OrderStatus;
import com.fullstockwh.shipment.enums.ShipmentStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ShipmentTrackResponse
{
    private String trackingNumber;
    private String carrierName;
    private ShipmentStatus shipmentStatus;
    private OrderStatus orderStatus;

    private BigDecimal shippingFee;
    private Double distanceKm;

    private Double destinationLat;
    private Double destinationLon;

    private String destinationCity;
    private String destinationDistrict;

    private LocalDateTime orderedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime deliveredAt;
}