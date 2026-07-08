package com.dinehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dinehub.entity.Product;
import com.dinehub.entity.Review;
import com.dinehub.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Get all reviews for a product
    List<Review> findByProduct(Product product);

    // Get all reviews by a user
    List<Review> findByUser(User user);

    // Check if a user has already reviewed a product
    Optional<Review> findByUserAndProduct(User user, Product product);

}