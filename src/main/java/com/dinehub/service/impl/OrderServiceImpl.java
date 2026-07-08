package com.dinehub.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dinehub.entity.Cart;
import com.dinehub.entity.Order;
import com.dinehub.entity.OrderItem;
import com.dinehub.entity.Product;
import com.dinehub.entity.User;
import com.dinehub.enums.OrderStatus;
import com.dinehub.repository.CartRepository;
import com.dinehub.repository.OrderRepository;
import com.dinehub.repository.ProductRepository;
import com.dinehub.repository.UserRepository;
import com.dinehub.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public Order placeOrder(String email,
                            String address,
                            String paymentMethod,
                            String paymentStatus,
                            BigDecimal totalAmount) {

        // ==========================
        // GET USER
        // ==========================
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // ==========================
        // GET CART
        // ==========================
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {

            throw new RuntimeException("Cart is empty");

        }

        // ==========================
        // CREATE ORDER
        // ==========================
        Order order = new Order();

        order.setOrderNumber(
                "ORD-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase());

        order.setInvoiceNumber(
                "INV-" + System.currentTimeMillis());

        order.setUser(user);

        order.setDeliveryAddress(address);

        order.setDeliveryPhone(user.getPhone());

        order.setPaymentMethod(paymentMethod);

        order.setPaymentStatus(paymentStatus);

        order.setStatus(OrderStatus.PENDING);

        order.setCreatedAt(LocalDateTime.now());

        order.setEstimatedDeliveryTime(
                LocalDateTime.now().plusMinutes(45));
        // ==========================
        // COPY CART ITEMS & UPDATE STOCK
        // ==========================

        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (var cartItem : cart.getCartItems()) {

            Product product = productRepository
                    .findById(cartItem.getProduct().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found"));

            // Product unavailable
            if (!Boolean.TRUE.equals(product.getAvailable())
                    || product.getQuantity() <= 0) {

                throw new RuntimeException(
                        product.getProductName()
                                + " is currently out of stock.");

            }

            // Customer ordered more than stock
            if (cartItem.getQuantity() > product.getQuantity()) {

                throw new RuntimeException(
                        "Only "
                                + product.getQuantity()
                                + " "
                                + product.getProductName()
                                + " available.");

            }

            // Reduce Stock
            product.setQuantity(
                    product.getQuantity() - cartItem.getQuantity());

            // Update availability
            if (product.getQuantity() <= 0) {

                product.setQuantity(0);

                product.setAvailable(false);

            } else {

                product.setAvailable(true);

            }

            productRepository.save(product);

            // Create Order Item
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProduct(product);
            
            orderItem.setProductName(product.getProductName());

            orderItem.setQuantity(cartItem.getQuantity());

            orderItem.setPrice(cartItem.getPrice());

            order.getOrderItems().add(orderItem);

            // Calculate total securely
            calculatedTotal = calculatedTotal.add(
                    cartItem.getPrice().multiply(
                            BigDecimal.valueOf(cartItem.getQuantity())
                    )
            );

        }

        order.setTotalAmount(calculatedTotal);
        // ==========================
        // SAVE ORDER
        // ==========================
        Order savedOrder = orderRepository.save(order);

        // ==========================
        // CLEAR CART
        // ==========================
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);

        cartRepository.save(cart);

        return savedOrder;
    }

}