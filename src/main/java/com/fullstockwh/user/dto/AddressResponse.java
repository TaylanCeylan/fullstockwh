package com.fullstockwh.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse
{
    private Long   id;
    private String addressTitle;
    private String buildingDetails;
    private String city;
    private String district;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
    private String neighborhood;

}
