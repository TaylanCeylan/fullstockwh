package com.fullstockwh.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateRequest
{
    @NotBlank(message = "Product name is required!")
    private String name;

    private String description;

    @NotNull(message = "Category is required!")
    private Long categoryId;

    @NotNull(message = "Price is required!")
    @Min(0)
    private BigDecimal price;

}
