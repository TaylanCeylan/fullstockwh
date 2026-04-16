package com.fullstockwh.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateRequest
{
    @NotBlank(message = "Category name is required!")
    private String name;
}
