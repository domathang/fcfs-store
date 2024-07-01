package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.dto.request.QuantityRequestDto;
import com.dony.fcfs_store.dto.request.ProductRequestDto;
import com.dony.fcfs_store.dto.response.CartProductResponse;
import com.dony.fcfs_store.dto.response.ProductResponseDto;
import com.dony.fcfs_store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    //TODO: 리스트 보기와 세부 정보 보기 차이 두기
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
    public void addToCart(@PathVariable Integer id, @RequestBody QuantityRequestDto dto) {
        productService.addToCart(id, dto);
    }

    @GetMapping("/cart")
    public List<CartProductResponse> getMyCartProduct() {
        return productService.getMyCartProduct();
    }

    @PatchMapping("/cart/{cartProductId}")
    public void changeCartProductQuantity(@PathVariable Integer cartProductId, @RequestBody QuantityRequestDto dto) {
        productService.changeCartProductQuantity(cartProductId, dto);
    }

    @DeleteMapping("/cart/{cartProductId}")
    public void deleteCartProduct(@PathVariable Integer cartProductId) {
        productService.deleteCartProduct(cartProductId);
    }
}
