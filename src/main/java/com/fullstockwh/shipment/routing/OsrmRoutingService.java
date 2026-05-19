package com.fullstockwh.shipment.routing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class OsrmRoutingService implements RoutingService
{

    private static final String OSRM_URL =
            "http://router.project-osrm.org/route/v1/driving/%s,%s;%s,%s?overview=false";

    private final RestTemplate restTemplate;

    @Override
    public Optional<DistanceResult> getDistance(double originLat, double originLon,
                                                double destLat,   double destLon)
    {

        String url = String.format(OSRM_URL, originLon, originLat, destLon, destLat);

        try {
            Map response = restTemplate.getForObject(url, Map.class);

            if (response == null || !"Ok".equals(response.get("code"))) {
                log.warn("OSRM did not respond or returned an error.");
                return Optional.empty();
            }

            List<Map> routes = (List<Map>) response.get("routes");
            if (routes == null || routes.isEmpty()) {
                log.warn("OSRM returned no routes.");
                return Optional.empty();
            }

            Map    route           = routes.get(0);
            double distanceMeters  = ((Number) route.get("distance")).doubleValue();
            double durationSeconds = ((Number) route.get("duration")).doubleValue();

            double distanceKm      = distanceMeters  / 1000.0;
            double durationMinutes = durationSeconds / 60.0;

            log.info("OSRM: distance={}km, duration={}min",
                    String.format("%.1f", distanceKm),
                    String.format("%.0f", durationMinutes));

            return Optional.of(new DistanceResult(distanceKm, durationMinutes));

        } catch (Exception e) {
            log.error("OSRM connection error: {}", e.getMessage());
            return Optional.empty();
        }
    }
}