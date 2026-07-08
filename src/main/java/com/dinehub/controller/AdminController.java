package com.dinehub.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.dinehub.entity.Order;
import com.dinehub.entity.Product;
import com.dinehub.entity.User;
import com.dinehub.enums.OrderStatus;
import com.dinehub.repository.OrderRepository;
import com.dinehub.repository.ProductRepository;
import com.dinehub.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // =========================================
    // ADMIN DASHBOARD
    // =========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        model.addAttribute("username", principal.getName());

        model.addAttribute("totalUsers",
                userRepository.count());

        model.addAttribute("totalProducts",
                productRepository.count());

        model.addAttribute("totalOrders",
                orderRepository.count());

        BigDecimal revenue = orderRepository.getTotalRevenue();

        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }

        model.addAttribute("totalRevenue", revenue);

        model.addAttribute("pendingOrders",
                orderRepository.countByStatus(OrderStatus.PENDING));

        model.addAttribute("confirmedOrders",
                orderRepository.countByStatus(OrderStatus.CONFIRMED));

        model.addAttribute("preparingOrders",
                orderRepository.countByStatus(OrderStatus.PREPARING));

        model.addAttribute("readyOrders",
                orderRepository.countByStatus(OrderStatus.READY));

        model.addAttribute("deliveryOrders",
                orderRepository.countByStatus(OrderStatus.OUT_FOR_DELIVERY));

        model.addAttribute("deliveredOrders",
                orderRepository.countByStatus(OrderStatus.DELIVERED));

        model.addAttribute("cancelledOrders",
                orderRepository.countByStatus(OrderStatus.CANCELLED));

        List<Order> latestOrders =
                orderRepository.findAllByOrderByCreatedAtDesc();

        if (latestOrders.size() > 5) {
            latestOrders = latestOrders.subList(0, 5);
        }

        model.addAttribute("latestOrders", latestOrders);

        return "admin-dashboard";
    }

    // =========================================
    // PRODUCTS
    // =========================================
    @GetMapping("/products")
    public String products(Model model) {

        model.addAttribute("products",
                productRepository.findAll());

        return "admin-products";
    }

    // =========================================
    // CUSTOMERS
    // =========================================
    @GetMapping("/customers")
    public String customers(
            @RequestParam(required = false) String keyword,
            Model model) {

        List<User> customers;

        if (keyword != null && !keyword.isBlank()) {

            customers =
                    userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            keyword,
                            keyword,
                            keyword);

            model.addAttribute("keyword", keyword);

        } else {

            customers = userRepository.findAll();

        }

        model.addAttribute("customers", customers);

        return "admin-customers";
    }

    // =========================================
    // ORDERS
    // =========================================
    @GetMapping("/orders")
    public String orders(Model model) {

        model.addAttribute("orders",
                orderRepository.findAllByOrderByCreatedAtDesc());

        model.addAttribute("statuses",
                OrderStatus.values());

        return "admin-orders";
    }

    // =========================================
    // ORDER DETAILS
    // =========================================
    @GetMapping("/orders/{id}")
    public String orderDetails(
            @PathVariable Long id,
            Model model) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        model.addAttribute("order", order);

        return "admin-order-details";
    }

    // =========================================
    // UPDATE STATUS
    // =========================================
    @PostMapping("/orders/update-status/{id}")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        order.setStatus(status);

        if (status == OrderStatus.DELIVERED) {
            order.setPaymentStatus("PAID");
        }

        orderRepository.save(order);

        return "redirect:/admin/orders";
    }

    // =========================================
    // CANCEL ORDER
    // =========================================
    @PostMapping("/orders/cancel/{id}")
    public String cancelOrder(
            @PathVariable Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        return "redirect:/admin/orders";
    }

}