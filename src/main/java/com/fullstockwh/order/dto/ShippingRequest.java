package com.fullstockwh.order.dto;

import lombok.Data;

@Data
public class ShippingRequest
{
    private Long addressId;
    private boolean saveAddress;
    private String addressTitle;
    private String city;
    private String district;
    private String neighborhood;
    private String fullAddress;
    private String buildingDetails;
    private Double latitude;
    private Double longitude;
}
