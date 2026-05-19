package com.fullstockwh.shipment.geocoding;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeocodingResult
{
    private Double latitude;
    private Double longitude;
}