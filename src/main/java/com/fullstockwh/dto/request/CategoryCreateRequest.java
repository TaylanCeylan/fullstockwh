package com.fullstockwh.dto.request;

import com.fullstockwh.enums.TargetGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CategoryCreateRequest
{
    @NotBlank(message = "Category name is required!")
    private String name;

    @NotBlank(message = "Category code is required!")
    @Size(min = 2, max = 5, message = "Code must be between 2 and 5 characters!")
    private String code;

    @NotNull(message = "Target gender is required for category!")
    private TargetGender targetGender;

    private List<String> requiredAttributes;
}
