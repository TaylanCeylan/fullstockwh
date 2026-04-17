package com.fullstockwh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ProductUpdateRequest
{
    @NotBlank(message = "Product name is required for update!")
    private String name;

    private String description;
    private String imageUrl;

    @NotNull(message = "CategoryID cannot be null!")
    private Long categoryId;

    private List<VariantUpdateRequest> variants;
}
