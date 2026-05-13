package com.fullstockwh.user.dto;

import lombok.Data;

@Data
public class AddressUpdateRequest
{
    private String addressTitle;
    private String buildingDetails;
    private String city;
    private String district;
    private String neighborhood;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
}
