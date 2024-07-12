package com.dony.fcfs_store.service;

import com.dony.common.exception.CustomException;
import com.dony.common.exception.ErrorCode;
import com.dony.fcfs_store.dto.response.ProductResponseDto;
import com.dony.fcfs_store.entity.Product;
import com.dony.fcfs_store.entity.Stock;
import com.dony.fcfs_store.repository.ProductRepository;
import com.dony.fcfs_store.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalProductService {
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public ProductResponseDto getProduct(Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Stock stock = stockRepository.findById(productId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return new ProductResponseDto(product, stock.getStock());
    }
}
