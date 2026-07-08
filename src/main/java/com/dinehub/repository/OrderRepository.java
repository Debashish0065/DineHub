package com.dinehub.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dinehub.entity.Order;
import com.dinehub.entity.User;
import com.dinehub.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ==========================
    // CUSTOMER ORDERS
    // ==========================
    @EntityGraph(attributePaths = {
            "orderItems",
            "orderItems.product",
            "user"
    })
    List<Order> findByUser(User user);

    // ==========================
    // ADMIN ORDERS
    // ==========================
    @EntityGraph(attributePaths = {
            "orderItems",
            "orderItems.product",
            "user"
    })
    List<Order> findAllByOrderByCreatedAtDesc();

    // ==========================
    // DASHBOARD
    // ==========================
    long countByStatus(OrderStatus status);

    @Query("""
            SELECT COALESCE(SUM(o.totalAmount),0)
            FROM Order o
            WHERE o.status='DELIVERED'
            """)
    BigDecimal getTotalRevenue();
}