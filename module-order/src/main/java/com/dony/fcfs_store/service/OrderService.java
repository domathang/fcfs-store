package com.dony.fcfs_store.service;

import com.dony.common.exception.CustomException;
import com.dony.common.exception.ErrorCode;
import com.dony.common.passport.UserContext;
import com.dony.fcfs_store.client.ApiClient;
import com.dony.fcfs_store.constant.OrderProductStatus;
import com.dony.fcfs_store.constant.OrderStatus;
import com.dony.fcfs_store.dto.request.CartOrderRequestDto;
import com.dony.fcfs_store.dto.request.QuantityRequestDto;
import com.dony.fcfs_store.dto.response.CartProductResponse;
import com.dony.fcfs_store.dto.response.OrderProductResponse;
import com.dony.fcfs_store.dto.response.OrderStatusResponse;
import com.dony.fcfs_store.dto.response.ProductResponse;
import com.dony.fcfs_store.entity.*;
import com.dony.fcfs_store.repository.CartProductRepository;
import com.dony.fcfs_store.repository.OrderProductRepository;
import com.dony.fcfs_store.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import static java.util.Objects.equals;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final CartProductRepository cartProductRepository;
    private final ApiClient apiClient;

    @Transactional
    public void createOrder(Integer productId, QuantityRequestDto dto) {
        UserContext currentUser = UserContext.getCurrentUser();
        ProductResponse product = apiClient.getProduct(productId);

        //TODO: 재고가 남아있는지 확인
        Order order = orderRepository.save(Order.builder()
                .customerId(currentUser.getUserId())
                .status(OrderStatus.ORDER_COMPLETED.status)
                .build());
        orderProductRepository.save(OrderProduct.builder()
                .order(order)
                .productId(productId)
                .quantity(dto.getQuantity())
                .totalPrice(product.getPrice() * dto.getQuantity())
                .build());
        //TODO: 남은 Product 의 재고 처리
    }

    @Transactional
    public void createCartOrder(CartOrderRequestDto dto) {
        UserContext currentUser = UserContext.getCurrentUser();

        Order order = orderRepository.save(Order.builder()
                .customerId(currentUser.getUserId())
                .status(OrderStatus.ORDER_COMPLETED.status)
                .build());

        List<Integer> selectedCartProductIdList = dto.getCartProductIdList();

        List<CartProduct> cartProducts = cartProductRepository.findByCustomerId(currentUser.getUserId()).stream().filter(
                cartProduct -> selectedCartProductIdList.contains(cartProduct.getId())
        ).toList();

        if (selectedCartProductIdList.size() > cartProducts.size())
            throw new CustomException(ErrorCode.BAD_REQUEST, "요청의 장바구니 상품 ID 리스트가 잘못됨");

        cartProducts.forEach(cartProduct -> {
            //TODO: 재고가 남아있는지 확인
            orderProductRepository.save(OrderProduct.builder()
                    .order(order)
                    .productId(cartProduct.getProductId())
                    .quantity(cartProduct.getQuantity())
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
                .map(orderProduct -> {
                    ProductResponse product = apiClient.getProduct(orderProduct.getProductId());
                    return OrderProductResponse.builder()
                            .productName(product.getName())
                            .price(product.getPrice())
                            .quantity(orderProduct.getQuantity())
                            .build();
                })
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
                    ProductResponse product = apiClient.getProduct(orderProduct.getProductId());
                    //TODO: 취소된 Product 의 재고 처리
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
                if (orderProduct.getProductId().equals(productId)) {
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
                //TODO: 반품 완료된 Product 의 재고 처리
//                orderProduct.getProduct().setStock(orderProduct.getProduct().getStock() + orderProduct.getQuantity());
            }
        });
    }

    public List<CartProductResponse> getMyCartProduct() {
        UserContext currentUser = UserContext.getCurrentUser();

        return cartProductRepository.findByCustomerId(currentUser.getUserId())
                .stream()
                .map(cartProduct -> new CartProductResponse(apiClient.getProduct(cartProduct.getProductId()), cartProduct.getQuantity()))
                .toList();
    }

    public void addToCart(Integer productId, QuantityRequestDto dto) {
        UserContext currentUser = UserContext.getCurrentUser();
        cartProductRepository.save(CartProduct.builder()
                .productId(productId)
                .quantity(dto.getQuantity())
                .customerId(currentUser.getUserId())
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
