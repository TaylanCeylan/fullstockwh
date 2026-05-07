package com.fullstockwh.product.review;

import com.fullstockwh.product.review.dto.ReviewCreateRequest;
import com.fullstockwh.product.review.dto.ReviewResponse;
import com.fullstockwh.product.Product;
import com.fullstockwh.user.UserEntity;
import com.fullstockwh.product.ProductRepository;
import com.fullstockwh.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ReviewServiceImpl implements ReviewService
{
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Override
    public ReviewResponse saveReview(ReviewCreateRequest request) {
        // 1. Find Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product cannot be found to be Reviewed!"));

        // 2. Find user in Service
        UserEntity userEntity = userService.findById(request.getUserId());

        // 3. Make Review Object
        Review review = Review.builder()
                .comment(request.getComment())
                .rating(request.getRating())
                .product(product)
                .userEntity(userEntity)
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
                .userName(review.getUserEntity().getUsername()) // or user.getEmail()
                .build();
    }
}
