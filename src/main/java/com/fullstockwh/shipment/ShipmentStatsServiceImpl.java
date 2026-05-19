package com.fullstockwh.shipment;

import com.fullstockwh.shipment.dto.ShipmentRowResponse;
import com.fullstockwh.shipment.dto.ShipmentStatsResponse;
import com.fullstockwh.shipment.enums.ShipmentStatus;
import com.fullstockwh.user.address.Address;
import com.fullstockwh.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
class ShipmentStatsServiceImpl implements ShipmentStatsService
{
    private final ShipmentRepository shipmentRepository;

    private static final double TREE_DAILY_ABSORPTION_KG = 0.05963;

    private static final double CAR_EMISSION_PER_KM = 0.21;

    @Override
    public ShipmentStatsResponse getStats(LocalDate from, LocalDate to)
    {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);

        Long   totalShipments = shipmentRepository.countByShippedAtBetween(start, end);
        Double totalDistance = shipmentRepository.sumDistanceKm(start, end);
        Double totalRevenue = shipmentRepository.sumShippingFee(start, end);
        Double totalCarbon = shipmentRepository.sumCarbonFootprint(start, end);

        if (totalDistance== null) totalDistance = 0.0;
        if (totalRevenue== null) totalRevenue  = 0.0;
        if (totalCarbon== null) totalCarbon   = 0.0;

        double treeDays = totalCarbon / TREE_DAILY_ABSORPTION_KG;
        double carKm = totalCarbon / CAR_EMISSION_PER_KM;

        Long shippedCount = shipmentRepository.countByStatusAndPeriod(ShipmentStatus.SHIPPED,   start, end);
        Long deliveredCount = shipmentRepository.countByStatusAndPeriod(ShipmentStatus.DELIVERED, start, end);

        List<Object[]> dailyRaw = shipmentRepository.getDailyCo2(start, end);
        List<String> dailyLabels = new ArrayList<>();
        List<Double> dailyCo2 = new ArrayList<>();
        for (Object[] row : dailyRaw) {
            dailyLabels.add((String) row[0]);
            dailyCo2.add(((Number) row[1]).doubleValue());
        }


        List<Object[]> cityRaw = shipmentRepository.getCityDistribution(start, end);
        List<String> cityLabels = new ArrayList<>();
        List<Long> cityCounts = new ArrayList<>();
        for (Object[] row : cityRaw) {
            cityLabels.add((String) row[0]);
            cityCounts.add(((Number) row[1]).longValue());
        }

        List<Shipment> shipments= shipmentRepository.findByShippedAtBetween(start, end);
        List<ShipmentRowResponse> rows = new ArrayList<>();
        for (Shipment s : shipments) {
            Order o= s.getOrder();
            Address a= o.getShippingAddress();
            rows.add(ShipmentRowResponse.builder()
                    .orderId(o.getId())
                    .trackingNumber(s.getTrackingNumber())
                    .customerFullName(o.getUser().getFirstName() + " " + o.getUser().getLastName())
                    .destinationCity(a != null ? a.getCity() : "-")
                    .distanceKm(s.getDistanceKm())
                    .shippingFee(s.getShippingFee())
                    .carbonFootprintKg(s.getCarbonFootprintKg())
                    .status(s.getStatus())
                    .shippedAt(s.getShippedAt())
                    .deliveredAt(s.getDeliveredAt())
                    .build());
        }

        return ShipmentStatsResponse.builder()
                .totalShipments(totalShipments)
                .totalDistanceKm(Math.round(totalDistance * 10.0) / 10.0)
                .totalRevenueKg(Math.round(totalRevenue * 100.0) / 100.0)
                .totalCarbonKg(Math.round(totalCarbon * 100.0) / 100.0)
                .treeDaysEquivalent(Math.round(treeDays * 10.0) / 10.0)
                .carKmEquivalent(Math.round(carKm * 10.0) / 10.0)
                .shippedCount(shippedCount)
                .deliveredCount(deliveredCount)
                .dailyLabels(dailyLabels)
                .dailyCo2(dailyCo2)
                .cityLabels(cityLabels)
                .cityCounts(cityCounts)
                .rows(rows)
                .build();
    }
}