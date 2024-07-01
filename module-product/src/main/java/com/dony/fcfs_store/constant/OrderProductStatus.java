package com.dony.fcfs_store.constant;

public enum OrderProductStatus {
    RETURN_ACCEPTED("return_accepted"),
    RETURN_COMPLETED("return_completed");

    public final String status;

    OrderProductStatus(String status) {
        this.status = status;
    }
}
