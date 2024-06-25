package com.dony.fcfs_store.dto;

import lombok.Getter;

@Getter
public class ProductRequestDto {
    private String name;
    private Integer price;
    private String imageUrl;
    private Integer stock;
    private String detail;
    private String saleStartTime;
}
