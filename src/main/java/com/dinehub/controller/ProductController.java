package com.dinehub.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dinehub.entity.Product;
import com.dinehub.entity.User;
import com.dinehub.repository.ProductRepository;
import com.dinehub.repository.ReviewRepository;
import com.dinehub.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @GetMapping("/products/{id}")
    public String productDetails(
            @PathVariable Long id,
            Model model,
            Principal principal) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        model.addAttribute("product", product);

        model.addAttribute(
                "reviews",
                reviewRepository.findByProduct(product));

        // Check whether the user has already reviewed
        boolean reviewed = false;

        if (principal != null) {

            User user = userRepository
                    .findByEmail(principal.getName())
                    .orElse(null);

            if (user != null) {

                reviewed = reviewRepository
                        .findByUserAndProduct(user, product)
                        .isPresent();
            }
        }

        model.addAttribute("alreadyReviewed", reviewed);

        return "product-details";
    }

}