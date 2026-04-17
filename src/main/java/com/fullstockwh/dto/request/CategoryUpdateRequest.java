package com.fullstockwh.dto.request;

import com.fullstockwh.enums.TargetGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CategoryUpdateRequest
{
    @NotBlank(message = "Category name is required for update!")
    private String name;

    @NotNull(message = "Target gender is required for update!")
    private TargetGender targetGender;

    private List<String> requiredAttributes;
}
