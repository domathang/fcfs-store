package com.dony.fcfs_store.service;

import com.dony.fcfs_store.dto.ProductRequestDto;
import com.dony.fcfs_store.dto.ProductResponseDto;
import com.dony.fcfs_store.entity.Product;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.ProductRepository;
import com.dony.fcfs_store.util.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final AuthenticationFacade authenticationFacade;

    public void registerProduct(ProductRequestDto dto) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        productRepository.save(Product.builder()
                        .owner(authenticationFacade.getLoggedInUser())
                        .detail(dto.getDetail())
                        .image_url(dto.getImageUrl())
                        .name(dto.getName())
                        .saleStartTime(LocalDateTime.parse(dto.getSaleStartTime(), dateTimeFormatter))
                        .price(dto.getPrice())
                        .stock(dto.getStock())
                .build());
    }

    public List<ProductResponseDto> getProductList() {
        Integer loggedInUserId = authenticationFacade.getLoggedInUserId();
        return productRepository.findAllByOwnerId(loggedInUserId)
                .stream().map(ProductResponseDto::new).toList();
    }

    public ProductResponseDto getProductDetail(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return new ProductResponseDto(product);
    }
}
