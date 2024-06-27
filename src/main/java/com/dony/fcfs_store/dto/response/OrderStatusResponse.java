package com.dony.fcfs_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class OrderStatusResponse {
    private Integer orderId;
    private List<OrderProductResponse> orderProducts;
    private String isDeliveryCompleted;
    private LocalDateTime orderedAt;
}
