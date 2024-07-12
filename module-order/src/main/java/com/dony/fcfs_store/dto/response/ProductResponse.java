package com.dony.fcfs_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private Integer price;
    private String imageUrl;
    private Integer stock;
    private String detail;
    private Boolean isOpen;
    private LocalDateTime saleStartTime;
    private LocalDateTime registeredAt;
}
