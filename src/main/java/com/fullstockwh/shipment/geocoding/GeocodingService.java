package com.fullstockwh.shipment.geocoding;

import java.util.Optional;

public interface GeocodingService
{

    Optional<GeocodingResult> geocode(String city, String district, String fullAddress);
}