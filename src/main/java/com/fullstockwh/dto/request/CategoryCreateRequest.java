package com.fullstockwh.dto.request;

import com.fullstockwh.enums.TargetGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryCreateRequest
{
    @NotBlank(message = "Category name is required!")
    private String name;

    @NotNull(message = "Target gender is required!")
    private TargetGender targetGender;
}
