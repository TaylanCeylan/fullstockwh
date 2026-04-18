package com.fullstockwh.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryUpdateRequest
{
    @NotBlank(message = "Category name cannot be empty for update!")
    private String name;

}
