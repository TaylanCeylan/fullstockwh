package com.fullstockwh.user.dto;

import lombok.Data;

@Data
public class AddressCreateRequest
{
    private String addressTitle;
    private String buildingDetails;
    private String city;
    private String district;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
    private String neighborhood;
    private boolean isTemporary = false;

}
