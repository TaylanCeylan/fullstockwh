package com.fullstockwh.shipment;

import com.fullstockwh.shipment.dto.ShipmentResponse;
import com.fullstockwh.shipment.dto.ShipmentTrackResponse;
import com.fullstockwh.shipment.dto.ShipOrderRequest;

public interface ShipmentService
{
    ShipmentResponse shipOrder(ShipOrderRequest request);

    ShipmentResponse markDelivered(Long orderId);

    ShipmentTrackResponse getTrackingInfo(Long orderId);
}