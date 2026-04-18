package com.fullstockwh.product.review;

import com.fullstockwh.product.review.dto.ReviewCreateRequest;
import com.fullstockwh.product.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController
{
    private final ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<ReviewResponse> addReview(@RequestBody ReviewCreateRequest request) {
        return ResponseEntity.ok(reviewService.saveReview(request));
    }
}
