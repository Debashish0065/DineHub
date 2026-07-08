package com.dinehub.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dinehub.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/products/{productId}/review")
    public String submitReview(
            @PathVariable Long productId,
            @RequestParam Integer rating,
            @RequestParam String comment,
            Principal principal) {

        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        reviewService.saveReview(
                productId,
                principal.getName(),
                rating,
                comment);

        return "redirect:/products/" + productId;
    }

}