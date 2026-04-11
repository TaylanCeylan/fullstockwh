package com.fullstockwh.controller;

import com.fullstockwh.dto.request.ReviewCreateRequest;
import com.fullstockwh.dto.response.ReviewResponse;
import com.fullstockwh.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController
{
    private final IReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<ReviewResponse> addReview(@RequestBody ReviewCreateRequest request) {
        return ResponseEntity.ok(reviewService.saveReview(request));
    }
}
