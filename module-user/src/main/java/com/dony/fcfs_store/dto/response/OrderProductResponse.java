package com.dony.fcfs_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class OrderProductResponse {
    private String productName;
    private Integer price;
    private Integer quantity;
}
