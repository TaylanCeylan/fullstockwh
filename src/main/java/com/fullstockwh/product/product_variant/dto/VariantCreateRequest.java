package com.fullstockwh.product.product_variant.dto;

import com.fullstockwh.product.product_variant.enums.Color;
import com.fullstockwh.product.product_variant.enums.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VariantCreateRequest
{
    @NotNull
    private Long productId;

    @NotNull
    private Color color;

    @NotNull
    private Size size;

    @Min(0)
    private Double price;

    @Min(0)
    private Integer stockQuantity;

    //logistics
    private Double weight;
    private Double width;
    private Double height;
    private Double length;
}
