package com.dinehub.service;

import java.math.BigDecimal;

import com.dinehub.entity.Order;

public interface OrderService {

    Order placeOrder(
            String email,
            String address,
            String paymentMethod,
            String paymentStatus,
            BigDecimal totalAmount);

}