package com.dony.fcfs_store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductRequestDto {
    private String name;
    private Integer price;
    private String imageUrl;
    private Integer stock;
    private String detail;
    private String saleStartTime;
}
