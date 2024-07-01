package com.dony.fcfs_store.constant;

public enum OrderStatus {
    ORDER_COMPLETED("order_completed"),
    ORDER_FAILURE("order_failure"),
    ORDER_CANCEL("order_cancel"),
    DELIVERING("delivering"),
    DELIVERY_COMPLETE("delivery_completed");

    public final String status;

    OrderStatus(String status) {
        this.status = status;
    }
}
