package com.dony.fcfs_store.service;

import com.dony.fcfs_store.dto.request.ProductRequestDto;
import com.dony.fcfs_store.dto.request.QuantityRequestDto;
import com.dony.fcfs_store.dto.response.CartProductResponse;
import com.dony.fcfs_store.dto.response.ProductResponseDto;
import com.dony.fcfs_store.entity.CartProduct;
import com.dony.fcfs_store.entity.Product;
import com.dony.fcfs_store.entity.User;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.CartProductRepository;
import com.dony.fcfs_store.repository.ProductRepository;
import com.dony.fcfs_store.util.AuthenticationFacade;
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
    private final AuthenticationFacade authenticationFacade;
    private final CartProductRepository cartProductRepository;

    public void registerProduct(ProductRequestDto dto) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
        productRepository.save(Product.builder()
                .owner(authenticationFacade.getLoggedInUser())
                .detail(dto.getDetail())
                .imageUrl(dto.getImageUrl())
                .name(dto.getName())
                .saleStartTime(LocalDateTime.parse(dto.getSaleStartTime(), dateTimeFormatter))
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build());
    }

    public List<CartProductResponse> getMyCartProduct() {
        Integer loggedInUserId = authenticationFacade.getLoggedInUserId();

        return cartProductRepository.findByCustomerId(loggedInUserId)
                .stream()
                .map(cartProduct -> new CartProductResponse(cartProduct.getProduct(), cartProduct.getQuantity()))
                .toList();
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

    public void addToCart(Integer productId, QuantityRequestDto dto) {
        User loggedinUser = authenticationFacade.getLoggedInUser();
        Product product = findProductById(productId);
        cartProductRepository.save(CartProduct.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .customer(loggedinUser)
                .build());
    }

    @Transactional
    public void changeCartProductQuantity(Integer cartProductId, QuantityRequestDto dto) {
        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        cartProduct.setQuantity(dto.getQuantity());
    }

    public void deleteCartProduct(Integer cartProductId) {
        cartProductRepository.deleteById(cartProductId);
    }

    private Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}
