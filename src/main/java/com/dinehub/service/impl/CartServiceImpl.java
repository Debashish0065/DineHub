package com.dinehub.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dinehub.entity.Cart;
import com.dinehub.entity.CartItem;
import com.dinehub.entity.Product;
import com.dinehub.entity.User;
import com.dinehub.repository.CartItemRepository;
import com.dinehub.repository.CartRepository;
import com.dinehub.repository.ProductRepository;
import com.dinehub.repository.UserRepository;
import com.dinehub.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Cart getCart(String email) {

        User user = getUser(email);

        return cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setCartItems(new ArrayList<>());
                    cart.setTotalAmount(BigDecimal.ZERO);

                    return cartRepository.save(cart);

                });
    }

    @Override
    public List<CartItem> getCartItems(String email) {
        return getCart(email).getCartItems();
    }

    @Override
    public BigDecimal getCartTotal(String email) {
        return getCart(email).getTotalAmount();
    }
    @Override
    public void addProduct(String email, Long productId) {

        Cart cart = getCart(email);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        if (cartItem == null) {

            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setPrice(product.getPrice());

            cart.getCartItems().add(cartItem);

        } else {

            cartItem.setQuantity(cartItem.getQuantity() + 1);

        }

        updateTotal(cart);

        cartRepository.save(cart);
    }

    @Override
    public void increaseQuantity(String email, Long cartItemId) {

        Cart cart = getCart(email);

        for (CartItem item : cart.getCartItems()) {

            if (item.getId().equals(cartItemId)) {

                item.setQuantity(item.getQuantity() + 1);

                break;
            }
        }

        updateTotal(cart);

        cartRepository.save(cart);
    }

    @Override
    public void decreaseQuantity(String email, Long cartItemId) {

        Cart cart = getCart(email);

        cart.getCartItems().removeIf(item -> {

            if (item.getId().equals(cartItemId)) {

                if (item.getQuantity() > 1) {

                    item.setQuantity(item.getQuantity() - 1);

                    return false;
                }

                return true;
            }

            return false;

        });

        updateTotal(cart);

        cartRepository.save(cart);
    }
    @Override
    public void removeItem(String email, Long cartItemId) {

        Cart cart = getCart(email);

        cart.getCartItems()
                .removeIf(item -> item.getId().equals(cartItemId));

        updateTotal(cart);

        cartRepository.save(cart);
    }

    @Override
    public void clearCart(String email) {

        Cart cart = getCart(email);

        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);

        cartRepository.save(cart);
    }

    private void updateTotal(Cart cart) {

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {

            total = total.add(
                    item.getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity())));
        }

        cart.setTotalAmount(total);
    }
}