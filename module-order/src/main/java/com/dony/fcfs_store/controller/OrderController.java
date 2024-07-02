package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.dto.request.CartOrderRequestDto;
import com.dony.fcfs_store.dto.request.QuantityRequestDto;
import com.dony.fcfs_store.dto.response.CartProductResponse;
import com.dony.fcfs_store.dto.response.OrderStatusResponse;
import com.dony.fcfs_store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/product/{id}/order")
    public void orderProduct(@PathVariable Integer id, @RequestBody QuantityRequestDto dto) {
        orderService.createOrder(id, dto);
    }

    @PostMapping("/cart/order")
    public void orderAllCartProduct(@RequestBody CartOrderRequestDto dto) {
        orderService.createCartOrder(dto);
    }

    @PostMapping("/product/{id}/cart")
    public void addToCart(@PathVariable Integer id, @RequestBody QuantityRequestDto dto) {
        orderService.addToCart(id, dto);
    }

    @GetMapping("/cart")
    public List<CartProductResponse> getMyCartProduct() {
        return orderService.getMyCartProduct();
    }

    @PatchMapping("/cart/{cartProductId}")
    public void changeCartProductQuantity(@PathVariable Integer cartProductId, @RequestBody QuantityRequestDto dto) {
        orderService.changeCartProductQuantity(cartProductId, dto);
    }

    @DeleteMapping("/cart/{cartProductId}")
    public void deleteCartProduct(@PathVariable Integer cartProductId) {
        orderService.deleteCartProduct(cartProductId);
    }

    @GetMapping("/order/{id}")
    public OrderStatusResponse checkOrderStatus(@PathVariable Integer id) {
        return orderService.checkOrderDelivery(id);
    }

    @DeleteMapping("/order/{id}")
    public void cancelOrder(@PathVariable Integer id) {
        orderService.cancelOrder(id);
    }

    @DeleteMapping("/order/{orderId}/product/{productId}")
    public void requestReturnProduct(@PathVariable Integer orderId, @PathVariable Integer productId) {
        orderService.requestReturnProduct(orderId, productId);
    }

    @GetMapping("/order/refresh")
    public void refreshReturnProductStatus() {
        orderService.refreshProductStock();
    }
}
