package com.fullstockwh.product.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse
{
    private Long id;
    private String comment;
    private Integer rating;
    private String userName;
    private String productName;
    private LocalDateTime createdAt;
}
