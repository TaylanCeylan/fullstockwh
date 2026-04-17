package com.fullstockwh.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import java.util.Map;

@Data
public class VariantUpdateRequest
{
    private Long id;

    private String sku;

    @Min(value = 0)
    private Double price;

    @Min(value = 0)
    private Integer stockQuantity;

    private Double weight, width, height, length;
    private Map<String, String> attributes;
}
