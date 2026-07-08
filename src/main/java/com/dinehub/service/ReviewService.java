package com.dinehub.service;

import java.util.List;

import com.dinehub.entity.Review;

public interface ReviewService {

    Review saveReview(
            Long productId,
            String email,
            Integer rating,
            String comment);

    List<Review> getProductReviews(Long productId);

    void deleteReview(Long reviewId);

}