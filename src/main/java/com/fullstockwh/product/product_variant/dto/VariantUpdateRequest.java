package com.fullstockwh.product.product_variant.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class VariantUpdateRequest
{
    @NotNull
    @Min(0)
    private Double price;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    //Logistics
    private Double weight;
    private Double width;
    private Double height;
    private Double length;
}
