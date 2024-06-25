package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.dto.ProductRequestDto;
import com.dony.fcfs_store.dto.ProductResponseDto;
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
        return productService.getProductList();
    }

    @GetMapping("/product/{id}")
    public ProductResponseDto getProductDetail(@PathVariable Integer id) {
        return productService.getProductDetail(id);
    }

    @PostMapping("/product")
    public void registerProduct(@RequestBody ProductRequestDto dto) {
        productService.registerProduct(dto);
    }
}
