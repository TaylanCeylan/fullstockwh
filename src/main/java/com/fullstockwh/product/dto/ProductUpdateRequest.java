package com.fullstockwh.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest
{
    private String name;
    private String description;
    private Long categoryId;
    private BigDecimal price;
}
