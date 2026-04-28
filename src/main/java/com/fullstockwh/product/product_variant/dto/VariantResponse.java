package com.fullstockwh.product.product_variant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantResponse
{
    private Long id;
    private String sku;
    private String color;
    private String size;
    private Integer stockQuantity;
    private Double unitWeight;

}
