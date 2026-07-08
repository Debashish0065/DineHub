package com.dinehub.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dinehub.entity.Product;
import com.dinehub.entity.Review;
import com.dinehub.entity.User;
import com.dinehub.repository.ProductRepository;
import com.dinehub.repository.ReviewRepository;
import com.dinehub.repository.UserRepository;
import com.dinehub.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Review saveReview(Long productId,
                             String email,
                             Integer rating,
                             String comment) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        // Prevent duplicate review
        if (reviewRepository.findByUserAndProduct(user, product).isPresent()) {
            throw new RuntimeException("You have already reviewed this product.");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(rating)
                .comment(comment)
                .build();

        reviewRepository.save(review);

        updateProductRating(product);

        return review;
    }

    @Override
    public List<Review> getProductReviews(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        return reviewRepository.findByProduct(product);
    }

    @Override
    public void deleteReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new RuntimeException("Review not found"));

        Product product = review.getProduct();

        reviewRepository.delete(review);

        updateProductRating(product);
    }

    // ==========================
    // UPDATE PRODUCT RATING
    // ==========================

    private void updateProductRating(Product product) {

        List<Review> reviews =
                reviewRepository.findByProduct(product);

        int totalReviews = reviews.size();

        double average = 0;

        if (totalReviews > 0) {

            int sum = reviews.stream()
                    .mapToInt(Review::getRating)
                    .sum();

            average = (double) sum / totalReviews;
        }

        product.setAverageRating(average);
        product.setTotalReviews(totalReviews);

        productRepository.save(product);
    }

}