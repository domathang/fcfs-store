package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.dto.request.CartOrderRequestDto;
import com.dony.fcfs_store.dto.request.CartProductRequestDto;
import com.dony.fcfs_store.dto.request.ProductRequestDto;
import com.dony.fcfs_store.dto.response.ProductResponseDto;
import com.dony.fcfs_store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/product/list")
    public List<ProductResponseDto> getProductList() {
        return productService.getOpendProductList();
    }

    @GetMapping("/product/{id}")
    public ProductResponseDto getProductDetail(@PathVariable Integer id) {
        return productService.getProductDetail(id);
    }

    @PostMapping("/product")
    public void registerProduct(@RequestBody ProductRequestDto dto) {
        productService.registerProduct(dto);
    }

    @PostMapping("/product/{id}/cart")
    public void addToCart(@PathVariable Integer id, @RequestBody CartProductRequestDto dto) {
        productService.addToCart(id, dto);
    }

    @PostMapping("/product/{id}/order")
    public void orderProduct(@PathVariable Integer id, @RequestBody CartProductRequestDto dto) {
        productService.createOrder(id, dto);
    }

    @PostMapping("/cart/order")
    public void orderAllCartProduct(@RequestBody CartOrderRequestDto dto) {
        productService.createCartOrder(dto);
    }
}
