package com.fullstockwh.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class VariantCreateRequest
{
    @NotBlank(message = "SKU cannot be null!")
    private String sku;

    @NotNull(message = "Price cannot be null!")
    @Min(value = 0, message = "Price cannot be lower than 0!")
    private Double price;

    @NotNull(message = "Stock cannot be empty")
    @Min(value = 0, message = "Stock cannot be negative!")
    private Integer stockQuantity;

    // Logistics attributes
    private Double weight;
    private Double width;
    private Double height;
    private Double length;

    // Category special attributes (Example: {"Color": "Black", "Size": "L"})
    private Map<String, String> attributes;
}
