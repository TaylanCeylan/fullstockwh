package com.fullstockwh.shipment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShipOrderRequest
{
    @NotNull(message = "Order ID is required!")
    private Long orderId;

    private String carrierName;
}