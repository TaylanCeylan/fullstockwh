package com.fullstockwh.dto.request;

import lombok.Data;

@Data
public class ProductCreateRequest
{
    private String name;
    private Double price;
    private String sku;
    private Integer stockQuantity;
    private Long categoryId;
}
