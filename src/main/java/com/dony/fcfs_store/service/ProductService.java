package com.dony.fcfs_store.service;

import com.dony.fcfs_store.constant.OrderStatus;
import com.dony.fcfs_store.dto.request.CartOrderRequestDto;
import com.dony.fcfs_store.dto.request.CartProductRequestDto;
import com.dony.fcfs_store.dto.request.ProductRequestDto;
import com.dony.fcfs_store.dto.response.ProductResponseDto;
import com.dony.fcfs_store.entity.*;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.CartProductRepository;
import com.dony.fcfs_store.repository.OrderProductRepository;
import com.dony.fcfs_store.repository.OrderRepository;
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
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

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

    public List<ProductResponseDto> getMyProductList() {
        Integer loggedInUserId = authenticationFacade.getLoggedInUserId();
        return productRepository.findAllByOwnerId(loggedInUserId)
                .stream()
                .map(ProductResponseDto::new)
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

    public void addToCart(Integer productId, CartProductRequestDto dto) {
        User loggedinUser = authenticationFacade.getLoggedInUser();
        Product product = findProductById(productId);
        cartProductRepository.save(CartProduct.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .customer(loggedinUser)
                .build());
    }

    @Transactional
    public void createOrder(Integer productId, CartProductRequestDto dto) {
        User loggedinUser = authenticationFacade.getLoggedInUser();
        Product product = findProductById(productId);
        //TODO: 재고가 남아있는지 확인
        Order order = orderRepository.save(Order.builder()
                .customer(loggedinUser)
                .status(OrderStatus.ORDER_COMPLETED.status)
                .build());
        orderProductRepository.save(OrderProduct.builder()
                .order(order)
                .product(product)
                .quantity(dto.getQuantity())
                .totalPrice(product.getPrice() * dto.getQuantity())
                .build());
        //TODO: 남은 Product의 재고 처리
    }

    @Transactional
    public void createCartOrder(CartOrderRequestDto dto) {
        User loggedinUser = authenticationFacade.getLoggedInUser();

        Order order = orderRepository.save(Order.builder()
                .customer(loggedinUser)
                .status(OrderStatus.ORDER_COMPLETED.status)
                .build());

        List<Integer> selectedProductIdList = dto.getProductIdList();

        List<CartProduct> cartProducts = cartProductRepository.findByCustomerId(loggedinUser.getId()).stream().filter(
                cartProduct -> selectedProductIdList.contains(cartProduct.getProduct().getId())
        ).toList();

        if (selectedProductIdList.size() != cartProducts.size())
            throw new CustomException(ErrorCode.BAD_REQUEST, "요청의 장바구니 상품 ID 리스트가 잘못됨");

        cartProducts.forEach(cartProduct -> {
                    //TODO: 재고가 남아있는지 확인
                    orderProductRepository.save(OrderProduct.builder()
                            .order(order)
                            .product(cartProduct.getProduct())
                            .quantity(cartProduct.getQuantity())
                            .totalPrice(cartProduct.getProduct()
                                    .getPrice() * cartProduct.getQuantity())
                            .build());
                    //TODO: 남은 Product의 재고 처리
                });

        cartProducts.forEach(cartProduct -> {
            cartProductRepository.deleteById(cartProduct.getId());
        });
    }

    private Product findProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}
