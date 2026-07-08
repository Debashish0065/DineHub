package com.dinehub.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dinehub.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===========================
    // ORDER INFORMATION
    // ===========================

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    // ===========================
    // CUSTOMER
    // ===========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ===========================
    // DELIVERY
    // ===========================

    @Column(nullable = false, length = 500)
    private String deliveryAddress;

    private String deliveryPhone;

    // ===========================
    // PAYMENT
    // ===========================

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private String paymentStatus;

    // ===========================
    // ORDER STATUS
    // ===========================

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    // ===========================
    // PRICE
    // ===========================

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // ===========================
    // ORDER ITEMS
    // ===========================

    @Default
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // ===========================
    // TRACKING
    // ===========================

    private LocalDateTime estimatedDeliveryTime;

    private LocalDateTime deliveredTime;

    @Column(length = 500)
    private String cancelledReason;

    @Column(length = 1000)
    private String remarks;

    // ===========================
    // TIMESTAMP
    // ===========================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (invoiceNumber == null || invoiceNumber.isBlank()) {
            invoiceNumber = "INV-" + System.currentTimeMillis();
        }

        if (estimatedDeliveryTime == null) {
            estimatedDeliveryTime = LocalDateTime.now().plusMinutes(45);
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();

        if (status == OrderStatus.DELIVERED && deliveredTime == null) {
            deliveredTime = LocalDateTime.now();
        }
    }
}