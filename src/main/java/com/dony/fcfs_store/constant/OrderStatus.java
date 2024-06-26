package com.dony.fcfs_store.constant;

public enum OrderStatus {
    ORDER_COMPLETED("order_completed"),
    ORDER_FAILURE("order_failure"),
    RETURN_ACCEPTED("return_accepted"),
    RETURN_COMPLETED("return_completed");

    public final String status;

    OrderStatus(String status) {
        this.status = status;
    }
}
