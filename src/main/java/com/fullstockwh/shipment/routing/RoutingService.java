package com.fullstockwh.shipment.routing;

import java.util.Optional;

public interface RoutingService
{
    Optional<DistanceResult> getDistance(double originLat, double originLon,
                                         double destLat,   double destLon);
}