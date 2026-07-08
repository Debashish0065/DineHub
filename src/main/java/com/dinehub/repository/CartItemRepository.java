package com.dinehub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dinehub.entity.Cart;
import com.dinehub.entity.CartItem;
import com.dinehub.entity.Product;

@Repository
public interface CartItemRepository
extends JpaRepository<CartItem, Long> {

    Optional<CartItem>
    findByCartAndProduct(Cart cart, Product product);

}