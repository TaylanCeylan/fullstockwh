package com.fullstockwh.product.review;

import com.fullstockwh.product.review.dto.ReviewCreateRequest;
import com.fullstockwh.product.review.dto.ReviewResponse;
import com.fullstockwh.user.UserEntity;
import java.util.List;

public interface ReviewService
{
    ReviewResponse saveReview(ReviewCreateRequest request, UserEntity user);

    List<ReviewResponse> getReviewsByProductId(Long productId);

    boolean canUserReview(Long productId, UserEntity user);

    void deleteReview(Long reviewId);

    List<ReviewResponse> getAllReviews();
}