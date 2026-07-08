package com.dinehub.enums;

public enum OrderStatus {

    PENDING("Order Placed"),
    CONFIRMED("Order Confirmed"),
    PREPARING("Preparing Food"),
    READY("Ready for Pickup"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}