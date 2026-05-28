package com.fullstockwh.product.review;

import com.fullstockwh.order.OrderRepository;
import com.fullstockwh.product.review.dto.ReviewCreateRequest;
import com.fullstockwh.product.review.dto.ReviewResponse;
import com.fullstockwh.product.Product;
import com.fullstockwh.user.UserEntity;
import com.fullstockwh.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ReviewServiceImpl implements ReviewService
{
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository   orderRepository;

    @Override
    public ReviewResponse saveReview(ReviewCreateRequest request, UserEntity user) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        if (!orderRepository.hasPurchasedProduct(user, request.getProductId()))
            throw new RuntimeException("You can only review products you have purchased.");

        if (reviewRepository.existsByProductIdAndUserEntityId(request.getProductId(), user.getId()))
            throw new RuntimeException("You have already reviewed this product.");

        Review review = Review.builder()
                .comment(request.getComment())
                .rating(request.getRating())
                .product(product)
                .userEntity(user)
                .build();

        reviewRepository.save(review);
        return mapToResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductIdWithUser(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean canUserReview(Long productId, UserEntity user) {
        if (user == null) return false;
        return orderRepository.hasPurchasedProduct(user, productId)
                && !reviewRepository.existsByProductIdAndUserEntityId(productId, user.getId());
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAllWithDetails().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToResponse(Review review) {
        UserEntity u = review.getUserEntity();
        String fullName = (u.getFirstName() != null ? u.getFirstName() : "")
                + " " + (u.getLastName() != null ? u.getLastName() : "");

        return ReviewResponse.builder()
                .id(review.getId())
                .comment(review.getComment())
                .rating(review.getRating())
                .userName(u.getUsername())
                .userFullName(fullName.trim().isEmpty() ? u.getUsername() : fullName.trim())
                .productName(review.getProduct().getName())
                .createdAt(review.getCreatedAt())
                .build();
    }
}