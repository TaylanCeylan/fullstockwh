package com.fullstockwh.dto.request;

import lombok.Data;

@Data
public class ProductUpdateRequest
{
    private String name;
    private String description;
    private Long categoryId;
}
