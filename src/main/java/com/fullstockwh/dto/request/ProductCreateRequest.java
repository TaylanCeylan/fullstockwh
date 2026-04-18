package com.fullstockwh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCreateRequest
{
    @NotBlank(message = "Product name is required!")
    private String name;

    private String description;

    @NotNull(message = "Category ID is required!")
    private Long categoryId;
}
