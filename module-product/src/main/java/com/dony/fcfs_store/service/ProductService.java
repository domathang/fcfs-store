package com.dony.fcfs_store.service;

import com.dony.common.exception.CustomException;
import com.dony.common.exception.ErrorCode;
import com.dony.fcfs_store.dto.request.ProductRequestDto;
import com.dony.fcfs_store.dto.response.ProductResponseDto;
import com.dony.fcfs_store.entity.Product;
import com.dony.fcfs_store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void registerProduct(ProductRequestDto dto) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
        productRepository.save(Product.builder()
//                .owner(authenticationFacade.getLoggedInUser())
                .detail(dto.getDetail())
                .imageUrl(dto.getImageUrl())
                .name(dto.getName())
                .saleStartTime(LocalDateTime.parse(dto.getSaleStartTime(), dateTimeFormatter))
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build());
    }

    public List<ProductResponseDto> getOpendProductList() {
        return productRepository.findByStockGreaterThanAndSaleStartTimeBefore(0, LocalDateTime.now())
                .stream()
                .map(ProductResponseDto::new)
                .toList();
    }

    public ProductResponseDto getProductDetail(Integer productId) {
        Product product = findProductById(productId);
        return new ProductResponseDto(product);
    }

    private Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}
