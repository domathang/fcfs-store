package com.dony.fcfs_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@Builder
public class CartProductResponse {
    private Integer productId;
    private String name;
    private Integer price;
    private Integer quantity;
    private String imageUrl;
    private LocalDateTime saleStartTime;

    public CartProductResponse(ProductResponse product, Integer quantity) {
        this.productId = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = quantity;
        this.imageUrl = product.getImageUrl();
        this.saleStartTime = product.getSaleStartTime();
    }
}