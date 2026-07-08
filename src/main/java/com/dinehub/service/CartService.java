package com.dinehub.service;

import java.math.BigDecimal;
import java.util.List;

import com.dinehub.entity.Cart;
import com.dinehub.entity.CartItem;

public interface CartService {

    Cart getCart(String email);

    List<CartItem> getCartItems(String email);

    BigDecimal getCartTotal(String email);

    void addProduct(String email, Long productId);

    void increaseQuantity(String email, Long cartItemId);

    void decreaseQuantity(String email, Long cartItemId);

    void removeItem(String email, Long cartItemId);

    void clearCart(String email);
}