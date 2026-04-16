package com.fullstockwh.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewCreateRequest
{
    private String comment;

    @NotNull(message = "rating is reqired!")
    @Min(1) @Max(5)
    private Integer rating;

    @NotNull(message = "Product Id is required!")
    private Long productId;

    @NotNull(message = "UserId is required!")
    private Long userId;
}
