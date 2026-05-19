package com.fullstockwh.shipment.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ShipmentStatsResponse
{

    private Long totalShipments;
    private Double totalDistanceKm;
    private Double totalRevenueKg;
    private Double totalCarbonKg;

    private double treeDaysEquivalent;
    private double carKmEquivalent;

    private Long shippedCount;
    private Long deliveredCount;

    private List<String> dailyLabels;
    private List<Double> dailyCo2;

    private List<String> cityLabels;
    private List<Long> cityCounts;

    private List<ShipmentRowResponse> rows;
}