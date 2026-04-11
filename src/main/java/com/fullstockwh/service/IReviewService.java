package com.fullstockwh.service;

import com.fullstockwh.dto.request.ReviewCreateRequest;
import com.fullstockwh.dto.response.ReviewResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

public interface IReviewService
{
    @PreAuthorize("hasRole('USER')") // Only Customers can Review
    ReviewResponse saveReview(ReviewCreateRequest request);

    List<ReviewResponse> getReviewsByProductId(Long productId);
}
