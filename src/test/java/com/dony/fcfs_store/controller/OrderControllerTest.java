package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.FcfsStoreApplication;
import com.dony.fcfs_store.constant.OrderProductStatus;
import com.dony.fcfs_store.constant.OrderStatus;
import com.dony.fcfs_store.dto.request.CartOrderRequestDto;
import com.dony.fcfs_store.dto.request.QuantityRequestDto;
import com.dony.fcfs_store.entity.*;
import com.dony.fcfs_store.entity.redis.TokenBlacklist;
import com.dony.fcfs_store.repository.*;
import com.dony.fcfs_store.repository.redis.TokenBlacklistRepository;
import com.dony.fcfs_store.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FcfsStoreApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private String token;
    private Product product;
    private CartProduct cartProduct;
    private Order order;
    private Order cancelOrder;
    private OrderProduct orderProduct;

    @BeforeEach
    public void setUp() {
        orderProductRepository.deleteAll();
        orderRepository.deleteAll();
        cartProductRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .email("test@example.com")
                .password("password")
                .username("testuser")
                .address("testaddress")
                .phone("1234567890")
                .build();
        userRepository.save(testUser);

        token = jwtUtil.createToken(testUser.getId());
        tokenBlacklistRepository.save(new TokenBlacklist(token, testUser.getId()));

        product = Product.builder()
                .owner(testUser)
                .stock(1)
                .name("productname")
                .price(1000)
                .detail("productdetail")
                .saleStartTime(LocalDateTime.now())
                .build();
        productRepository.save(product);

        cartProduct = CartProduct.builder()
                .customer(testUser)
                .product(product)
                .quantity(1)
                .build();
        cartProductRepository.save(cartProduct);

        order = Order.builder()
                .orderedAt(LocalDateTime.now().minusDays(3))
                .customer(testUser)
                .status(OrderStatus.ORDER_COMPLETED.status)
                .deliveryCompletedAt(LocalDateTime.now())
                .build();
        cancelOrder = Order.builder()
                .orderedAt(LocalDateTime.now())
                .customer(testUser)
                .status(OrderStatus.ORDER_COMPLETED.status)
                .build();
        orderRepository.save(order);
        orderRepository.save(cancelOrder);

        orderProduct = OrderProduct.builder()
                .totalPrice(product.getPrice())
                .quantity(2)
                .product(product)
                .order(order)
                .returnAcceptedAt(LocalDateTime.now().minusDays(2))
                .build();
        orderProductRepository.save(orderProduct);
    }

    @Test
    public void testOrderProduct() throws Exception {
        QuantityRequestDto quantityRequestDto = new QuantityRequestDto(2);
        mockMvc.perform(post("/product/" + product.getId() + "/order")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quantityRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testOrderAllCartProduct() throws Exception {
        CartOrderRequestDto cartOrderRequestDto = new CartOrderRequestDto(new ArrayList<>(cartProduct.getId()));
        mockMvc.perform(post("/cart/order")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartOrderRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckOrderStatus() throws Exception {
        mockMvc.perform(get("/order/" + order.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatus.DELIVERY_COMPLETE.status));
    }

    @Test
    public void testCancelOrderFailure() throws Exception {
        mockMvc.perform(delete("/order/" + order.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCancelOrder() throws Exception {
        mockMvc.perform(delete("/order/" + cancelOrder.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertTrue(orderRepository.findById(cancelOrder.getId()).isPresent());
        assertEquals(orderRepository.findById(cancelOrder.getId()).get().getStatus(), OrderStatus.ORDER_CANCEL.status);
    }

    @Test
    public void testRequestReturnProduct() throws Exception {
        mockMvc.perform(get("/order/" + order.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/order/" + order.getId() + "/product/" + product.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertTrue(orderProductRepository.findById(orderProduct.getId()).isPresent());
        assertEquals(orderProductRepository.findById(orderProduct.getId()).get().getStatus(),
                OrderProductStatus.RETURN_ACCEPTED.status);
    }

    @Test
    public void testRequestReturnProductFailure() throws Exception {
        order.setDeliveryCompletedAt(LocalDateTime.now().minusDays(3));
        orderRepository.save(order);
        mockMvc.perform(delete("/order/" + order.getId() + "/product/" + product.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        assertTrue(orderProductRepository.findById(orderProduct.getId()).isPresent());
        assertNull(orderProductRepository.findById(orderProduct.getId())
                .get()
                .getStatus());
    }

    @Test
    public void testRefreshProductStock() throws Exception {
        mockMvc.perform(get("/order/" + order.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/order/" + order.getId() + "/product/" + product.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        OrderProduct orderProduct1 = orderProductRepository.findById(orderProduct.getId())
                .get();
        orderProduct1.setReturnAcceptedAt(LocalDateTime.now().minusDays(2));
        orderProductRepository.save(orderProduct1);

        mockMvc.perform(get("/order/refresh")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        assertTrue(orderProductRepository.findById(orderProduct.getId()).isPresent());
        assertEquals(orderProductRepository.findById(orderProduct.getId()).get().getStatus(),
                OrderProductStatus.RETURN_COMPLETED.status);
    }
}
