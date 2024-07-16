package com.dony.fcfs_store.service;

import com.dony.common.exception.CustomException;
import com.dony.common.exception.ErrorCode;
import com.dony.common.passport.UserContext;
import com.dony.fcfs_store.dto.request.ProductRequestDto;
import com.dony.fcfs_store.dto.response.ProductResponseDto;
import com.dony.fcfs_store.entity.Product;
import com.dony.fcfs_store.entity.Stock;
import com.dony.fcfs_store.repository.ProductRepository;
import com.dony.fcfs_store.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Transactional
    public void registerProduct(ProductRequestDto dto) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
        Product product = productRepository.save(Product.builder()
                .ownerId(UserContext.getCurrentUser()
                        .getUserId())
                .detail(dto.getDetail())
                .imageUrl(dto.getImageUrl())
                .name(dto.getName())
                .saleStartTime(LocalDateTime.parse(dto.getSaleStartTime(), dateTimeFormatter))
                .price(dto.getPrice())
                .build());

        stockRepository.save(Stock.builder()
                .stock(dto.getStock())
                .product(product)
                .build());
    }

    public List<ProductResponseDto> getOpendProductList() {
        return productRepository.findBySaleStartTimeBefore(LocalDateTime.now())
                .stream()
                .map(product -> new ProductResponseDto(product, findStockByProductId(product.getId()).getId()))
                .toList();
    }

    public ProductResponseDto getProductDetail(Integer productId) {
        Product product = findProductById(productId);
        Stock stock = findStockByProductId(productId);
        return new ProductResponseDto(product, stock.getStock());
    }

    private Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    private Stock findStockByProductId(Integer id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}
