package com.fullstockwh.shipment.routing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DistanceResult
{
    private Double distanceKm;
    private Double durationMinutes;
}