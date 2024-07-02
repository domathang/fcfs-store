package com.dony.fcfs_store.service;

import com.dony.fcfs_store.constant.OrderProductStatus;
import com.dony.fcfs_store.constant.OrderStatus;
import com.dony.fcfs_store.dto.request.CartOrderRequestDto;
import com.dony.fcfs_store.dto.request.QuantityRequestDto;
import com.dony.fcfs_store.dto.response.CartProductResponse;
import com.dony.fcfs_store.dto.response.OrderProductResponse;
import com.dony.fcfs_store.dto.response.OrderStatusResponse;
import com.dony.fcfs_store.entity.*;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.CartProductRepository;
import com.dony.fcfs_store.repository.OrderProductRepository;
import com.dony.fcfs_store.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final CartProductRepository cartProductRepository;

    @Transactional
    public void createOrder(Integer productId, QuantityRequestDto dto) {
//        User loggedInUser = authenticationFacade.getLoggedInUser();
        Product product = new Product();
        //TODO: 재고가 남아있는지 확인
        Order order = orderRepository.save(Order.builder()
//                .customer(loggedInUser)
                .status(OrderStatus.ORDER_COMPLETED.status)
                .build());
        orderProductRepository.save(OrderProduct.builder()
                .order(order)
                .product(product)
                .quantity(dto.getQuantity())
                .totalPrice(product.getPrice() * dto.getQuantity())
                .build());
        //TODO: 남은 Product 의 재고 처리
    }

    @Transactional
    public void createCartOrder(CartOrderRequestDto dto) {
//        User loggedInUser = authenticationFacade.getLoggedInUser();

        Order order = orderRepository.save(Order.builder()
//                .customer(loggedInUser)
                .status(OrderStatus.ORDER_COMPLETED.status)
                .build());

        List<Integer> selectedCartProductIdList = dto.getCartProductIdList();

//        List<CartProduct> cartProducts = cartProductRepository.findByCustomerId(loggedInUser.getId()).stream().filter(
//                cartProduct -> selectedCartProductIdList.contains(cartProduct.getId())
//        ).toList();
        List<CartProduct> cartProducts = new ArrayList<>();

        if (selectedCartProductIdList.size() > cartProducts.size())
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
            //TODO: 남은 Product 의 재고 처리
        });

        cartProducts.forEach(cartProduct -> {
            cartProductRepository.deleteById(cartProduct.getId());
        });
    }

    @Transactional
    public OrderStatusResponse checkOrderDelivery(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (order.getOrderedAt()
                .plusDays(2)
                .isBefore(LocalDateTime.now())) {
            order.setStatus(OrderStatus.DELIVERY_COMPLETE.status);
            order.setDeliveryCompletedAt(LocalDateTime.now());
        } else if (order.getOrderedAt()
                .plusDays(1)
                .isBefore(LocalDateTime.now())) {
            order.setStatus(OrderStatus.DELIVERING.status);
        }

        List<OrderProductResponse> orderProductList = order.getOrderProducts()
                .stream()
                .map(orderProduct -> OrderProductResponse.builder()
                        .productName(orderProduct.getProduct()
                                .getName())
                        .price(orderProduct.getProduct()
                                .getPrice())
                        .quantity(orderProduct.getQuantity())
                        .build())
                .toList();

        return OrderStatusResponse.builder()
                .orderId(order.getId())
                .orderedAt(order.getOrderedAt())
                .status(order.getStatus())
                .orderProducts(orderProductList)
                .build();
    }

    @Transactional
    public void cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (order.getOrderedAt()
                .plusDays(1)
                .isBefore(LocalDateTime.now()))
            throw new CustomException(ErrorCode.BAD_REQUEST, "배송이 시작된 이후에는 주문을 취소할 수 없음");

        order.setStatus(OrderStatus.ORDER_CANCEL.status);

        order.getOrderProducts()
                .forEach(orderProduct -> {
                    Product product = orderProduct.getProduct();
                    product.setStock(product.getStock() + orderProduct.getQuantity());
                });
    }

    @Transactional
    public void requestReturnProduct(Integer orderId, Integer productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (order.getOrderedAt()
                .plusDays(2)
                .isAfter(LocalDateTime.now())) {
            order.setStatus(OrderStatus.DELIVERY_COMPLETE.status);
            order.setDeliveryCompletedAt(LocalDateTime.now());
        } else if (order.getOrderedAt()
                .plusDays(1)
                .isAfter(LocalDateTime.now())) {
            order.setStatus(OrderStatus.DELIVERING.status);
        }

        if (order.getStatus().equals(OrderStatus.DELIVERY_COMPLETE.status) &&
                order.getDeliveryCompletedAt().plusDays(1).isAfter(LocalDateTime.now())) {
            order.getOrderProducts().forEach(orderProduct -> {
                if (Objects.equals(orderProduct.getProduct()
                        .getId(), productId)) {
                    orderProduct.setStatus(OrderProductStatus.RETURN_ACCEPTED.status);
                    orderProduct.setReturnAcceptedAt(LocalDateTime.now());
                }
            });
        } else {
            throw new CustomException(ErrorCode.BAD_REQUEST, "배송이 완료되고 하루가 지나면 반품할 수 없음");
        }
    }

    @Transactional
    public void refreshProductStock() {
        List<OrderProduct> returnAcceptedOrderProducts =
                orderProductRepository.findByStatus(OrderProductStatus.RETURN_ACCEPTED.status);
        returnAcceptedOrderProducts.forEach(orderProduct -> {
            if (LocalDateTime.now().isAfter(orderProduct.getReturnAcceptedAt().plusDays(1))) {
                orderProduct.setStatus(OrderProductStatus.RETURN_COMPLETED.status);
                orderProduct.setReturnCompletedAt(LocalDateTime.now());
                orderProduct.getProduct().setStock(orderProduct.getProduct().getStock() + orderProduct.getQuantity());
            }
        });
    }

    public List<CartProductResponse> getMyCartProduct() {
//        Integer loggedInUserId = authenticationFacade.getLoggedInUserId();
        Integer loggedInUserId = 1;

        return cartProductRepository.findByCustomerId(loggedInUserId)
                .stream()
                .map(cartProduct -> new CartProductResponse(cartProduct.getProduct(), cartProduct.getQuantity()))
                .toList();
    }

    public void addToCart(Integer productId, QuantityRequestDto dto) {
//        User loggedInUser = authenticationFacade.getLoggedInUser();
        Product product = new Product();
        cartProductRepository.save(CartProduct.builder()
                .product(product)
                .quantity(dto.getQuantity())
//                .customer(loggedInUser)
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
}
