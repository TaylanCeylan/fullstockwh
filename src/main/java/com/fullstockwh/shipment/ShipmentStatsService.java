package com.fullstockwh.shipment;

import com.fullstockwh.shipment.dto.ShipmentStatsResponse;
import java.time.LocalDate;

public interface ShipmentStatsService
{
    ShipmentStatsResponse getStats(LocalDate from, LocalDate to);
}