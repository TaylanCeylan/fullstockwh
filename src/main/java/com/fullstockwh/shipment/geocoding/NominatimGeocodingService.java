package com.fullstockwh.shipment.geocoding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class NominatimGeocodingService implements GeocodingService
{
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    private final RestTemplate restTemplate;

    @Override
    public Optional<GeocodingResult> geocode(String city, String district, String fullAddress)
    {

        String query = String.join(", ", fullAddress, district, city, "Turkey");

        String url = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
                .queryParam("q",            query)
                .queryParam("format",       "json")
                .queryParam("limit",        1)
                .queryParam("countrycodes", "tr")
                .toUriString();

        try {

            Thread.sleep(1000);

            Map[] results = restTemplate.getForObject(url, Map[].class);

            if (results == null || results.length == 0) {
                log.warn("Nominatim: no result for full address, retrying with city and district. Query: {}", query);
                return geocodeFallback(city, district);
            }

            Double lat = Double.parseDouble((String) results[0].get("lat"));
            Double lon = Double.parseDouble((String) results[0].get("lon"));
            log.info("Nominatim: coordinates resolved - lat={}, lon={}", lat, lon);

            return Optional.of(new GeocodingResult(lat, lon));

        } catch (Exception e) {
            log.error("Nominatim geocoding error: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<GeocodingResult> geocodeFallback(String city, String district)
    {
        String fallbackQuery = district + ", " + city + ", Turkey";

        String url = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
                .queryParam("q",            fallbackQuery)
                .queryParam("format",       "json")
                .queryParam("limit",        1)
                .queryParam("countrycodes", "tr")
                .toUriString();

        try {
            Map[] results = restTemplate.getForObject(url, Map[].class);

            if (results == null || results.length == 0) {
                log.warn("Nominatim fallback returned no results. Query: {}", fallbackQuery);
                return Optional.empty();
            }

            Double lat = Double.parseDouble((String) results[0].get("lat"));
            Double lon = Double.parseDouble((String) results[0].get("lon"));
            return Optional.of(new GeocodingResult(lat, lon));

        } catch (Exception e) {
            log.error("Nominatim fallback error: {}", e.getMessage());
            return Optional.empty();
        }
    }
}