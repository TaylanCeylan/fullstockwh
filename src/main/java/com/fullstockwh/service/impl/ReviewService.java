package com.fullstockwh.service.impl;

import com.fullstockwh.dto.request.ReviewCreateRequest;
import com.fullstockwh.dto.response.ReviewResponse;
import com.fullstockwh.entity.Product;
import com.fullstockwh.entity.Review;
import com.fullstockwh.entity.User;
import com.fullstockwh.repository.IProductRepository;
import com.fullstockwh.repository.IReviewRepository;
import com.fullstockwh.service.IReviewService;
import com.fullstockwh.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService
{
    private final IReviewRepository reviewRepository;
    private final IProductRepository productRepository;
    private final IUserService userService;

    @Override
    public ReviewResponse saveReview(ReviewCreateRequest request) {
        // 1. Find Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product cannot be found to be Reviewed!"));

        // 2. Find user in Service
        User user = userService.findById(request.getUserId());

        // 3. Make Review Object
        Review review = Review.builder()
                .comment(request.getComment())
                .rating(request.getRating())
                .product(product)
                .user(user)
                .build();

        reviewRepository.save(review);
        return mapToResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .comment(review.getComment())
                .rating(review.getRating())
                .productName(review.getProduct().getName())
                .userName(review.getUser().getUsername()) // or user.getEmail()
                .build();
    }
}
