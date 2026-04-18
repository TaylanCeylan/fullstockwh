package com.fullstockwh.product.review;

import com.fullstockwh.product.review.dto.ReviewCreateRequest;
import com.fullstockwh.product.review.dto.ReviewResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

public interface ReviewService
{
    @PreAuthorize("hasRole('USER')") // Only Customers can Review
    ReviewResponse saveReview(ReviewCreateRequest request);

    List<ReviewResponse> getReviewsByProductId(Long productId);
}
