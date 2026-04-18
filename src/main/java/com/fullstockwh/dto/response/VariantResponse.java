package com.fullstockwh.dto.response;

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
    private Double price;
    private Integer stockQuantity;
    private Double weight;
    private Double width;
    private Double height;
    private Double length;
}
