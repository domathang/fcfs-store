package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.FcfsStoreApplication;
import com.dony.fcfs_store.dto.request.ProductRequestDto;
import com.dony.fcfs_store.dto.request.QuantityRequestDto;
import com.dony.fcfs_store.entity.CartProduct;
import com.dony.fcfs_store.entity.Product;
import com.dony.fcfs_store.entity.User;
import com.dony.fcfs_store.entity.redis.TokenBlacklist;
import com.dony.fcfs_store.repository.CartProductRepository;
import com.dony.fcfs_store.repository.ProductRepository;
import com.dony.fcfs_store.repository.UserRepository;
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
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FcfsStoreApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerTest {
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
    private JwtUtil jwtUtil;

    private User testUser;
    private String token;
    private Product product;
    private CartProduct cartProduct;

    @BeforeEach
    public void setUp() {
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
        tokenBlacklistRepository.save(new TokenBlacklist(token, testUser.getId(), true));

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
    }

    @Test
    public void testGetProductList() throws Exception {
        mockMvc.perform(get("/product/list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(product.getName()))
                .andExpect(jsonPath("$[0].price").value(product.getPrice()));
    }

    @Test
    public void testGetProductDetail() throws Exception {
        mockMvc.perform(get("/product/" + product.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice()));
    }

    @Test
    public void testRegisterProduct() throws Exception {
        ProductRequestDto productRequestDto = new ProductRequestDto("Product 1", 100, null, 1, "product1detail",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuu-MM-dd'T'HH:mm:ss")));

        mockMvc.perform(post("/product")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDto)))
                .andExpect(status().isOk());

        assertEquals(productRepository.findAll().size(), 2);
    }

    @Test
    public void testAddToCart() throws Exception {
        QuantityRequestDto quantityRequestDto = new QuantityRequestDto(2);

        mockMvc.perform(post("/product/" + product.getId() +"/cart")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quantityRequestDto)))
                .andExpect(status().isOk());

        assertFalse(cartProductRepository.findByCustomerId(testUser.getId())
                .isEmpty());
    }

    @Test
    public void testGetMyCartProduct() throws Exception {
        mockMvc.perform(get("/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(product.getName()))
                .andExpect(jsonPath("$[0].quantity").value(1));
    }

    @Test
    public void testChangeCartProductQuantity() throws Exception {
        QuantityRequestDto quantityRequestDto = new QuantityRequestDto(cartProduct.getQuantity() + 1);

        mockMvc.perform(patch("/cart/" + cartProduct.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quantityRequestDto)))
                .andExpect(status().isOk());

        assertTrue(cartProductRepository.findByCustomerId(testUser.getId()).getFirst().getQuantity() > cartProduct.getQuantity());
    }

    @Test
    public void testDeleteCartProduct() throws Exception {
        mockMvc.perform(delete("/cart/" + cartProduct.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertTrue(cartProductRepository.findByCustomerId(testUser.getId()).isEmpty());
    }

}
