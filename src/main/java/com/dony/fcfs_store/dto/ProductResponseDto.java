package com.dony.fcfs_store.dto;

import com.dony.fcfs_store.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Integer id;
    private String name;
    private Integer price;
    private String imageUrl;
    private Integer stock;
    private String detail;
    private LocalDateTime saleStartTime;
    private LocalDateTime registeredAt;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.imageUrl = product.getImage_url();
        this.stock = product.getStock();
        this.detail = product.getDetail();
        this.saleStartTime = product.getSaleStartTime();
        this.registeredAt = product.getRegisteredAt();
    }
}
