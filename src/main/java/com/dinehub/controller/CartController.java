package com.dinehub.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dinehub.entity.Cart;
import com.dinehub.entity.CartItem;
import com.dinehub.entity.Product;
import com.dinehub.entity.User;
import com.dinehub.repository.CartRepository;
import com.dinehub.repository.ProductRepository;
import com.dinehub.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // ======================================
    // ADD TO CART
    // ======================================
    @PostMapping("/add-to-cart/{productId}")
    public String addToCart(@PathVariable Long productId,
                            Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow();

        Product product = productRepository
                .findById(productId)
                .orElseThrow();

        // Prevent adding unavailable products
        if (!Boolean.TRUE.equals(product.getAvailable())
                || product.getQuantity() <= 0) {

            return "redirect:/menu?outofstock";
        }

        Cart cart = cartRepository
                .findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);
                    newCart.setCartItems(new ArrayList<>());
                    newCart.setTotalAmount(BigDecimal.ZERO);

                    return cartRepository.save(newCart);

                });

        Optional<CartItem> existingItem =
                cart.getCartItems()
                        .stream()
                        .filter(item ->
                                item.getProduct()
                                        .getId()
                                        .equals(productId))
                        .findFirst();

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();

            if (item.getQuantity() >= product.getQuantity()) {

                return "redirect:/cart?stocklimit";

            }

            item.setQuantity(item.getQuantity() + 1);

        } else {

            CartItem cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setPrice(product.getPrice());

            cart.getCartItems().add(cartItem);

        }

        updateCartTotal(cart);

        cartRepository.save(cart);

        return "redirect:/cart";

    }

    // ======================================
    // VIEW CART
    // ======================================
    @GetMapping("/cart")
    public String cart(Model model,
                       Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow();

        Cart cart = cartRepository
                .findByUser(user)
                .orElse(null);

        List<CartItem> cartItems = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        if (cart != null) {

            cartItems = cart.getCartItems();

            totalAmount = cart.getTotalAmount();

        }

        model.addAttribute("cartItems", cartItems);

        model.addAttribute("totalAmount", totalAmount);

        return "cart";

    }

    // ======================================
    // CHECKOUT
    // ======================================
    @GetMapping("/checkout")
    public String checkout(Model model,
                           Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow();

        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow();

        model.addAttribute("cartItems", cart.getCartItems());

        model.addAttribute("totalAmount",
                cart.getTotalAmount());

        return "checkout";

    }
    // ======================================
    // INCREASE QUANTITY
    // ======================================
    @PostMapping("/increase/{id}")
    public String increaseQuantity(@PathVariable Long id,
                                   Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow();

        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow();

        for (CartItem item : cart.getCartItems()) {

            if (item.getId().equals(id)) {

                Product product = item.getProduct();

                // Prevent increasing beyond available stock
                if (item.getQuantity() < product.getQuantity()) {

                    item.setQuantity(item.getQuantity() + 1);

                }

                break;
            }
        }

        updateCartTotal(cart);

        cartRepository.save(cart);

        return "redirect:/cart";

    }

    // ======================================
    // DECREASE QUANTITY
    // ======================================
    @PostMapping("/decrease/{id}")
    public String decreaseQuantity(@PathVariable Long id,
                                   Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow();

        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow();

        cart.getCartItems().removeIf(item -> {

            if (item.getId().equals(id)) {

                if (item.getQuantity() > 1) {

                    item.setQuantity(item.getQuantity() - 1);

                    return false;

                }

                return true;

            }

            return false;

        });

        updateCartTotal(cart);

        cartRepository.save(cart);

        return "redirect:/cart";

    }

    // ======================================
    // REMOVE FROM CART
    // ======================================
    @PostMapping("/remove-from-cart/{cartItemId}")
    public String removeFromCart(@PathVariable Long cartItemId,
                                 Principal principal) {

        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow();

        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow();

        cart.getCartItems().removeIf(item ->
                item.getId().equals(cartItemId));

        updateCartTotal(cart);

        cartRepository.save(cart);

        return "redirect:/cart";

    }
    // ======================================
    // UPDATE CART TOTAL
    // ======================================
    private void updateCartTotal(Cart cart) {

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {

            total = total.add(
                    item.getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    )
            );

        }

        cart.setTotalAmount(total);

    }

}