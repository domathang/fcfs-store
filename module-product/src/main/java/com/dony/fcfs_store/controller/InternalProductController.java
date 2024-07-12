package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.dto.response.ProductResponseDto;
import com.dony.fcfs_store.service.InternalProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalProductController {
    private final InternalProductService productService;

    @GetMapping("/internal/api/product/{id}")
    public ProductResponseDto getProduct(@PathVariable Integer id) {
        return productService.getProduct(id);
    }
}
