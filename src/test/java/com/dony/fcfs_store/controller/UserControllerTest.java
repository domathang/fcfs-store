package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.FcfsStoreApplication;
import com.dony.fcfs_store.dto.request.LoginDto;
import com.dony.fcfs_store.dto.request.UpdatePasswordDto;
import com.dony.fcfs_store.dto.request.UserRequestDto;
import com.dony.fcfs_store.entity.User;
import com.dony.fcfs_store.entity.redis.EmailAvailable;
import com.dony.fcfs_store.entity.redis.TokenBlacklist;
import com.dony.fcfs_store.repository.UserRepository;
import com.dony.fcfs_store.repository.redis.EmailAvailableRepository;
import com.dony.fcfs_store.repository.redis.TokenBlacklistRepository;
import com.dony.fcfs_store.util.CryptoUtil;
import com.dony.fcfs_store.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FcfsStoreApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailAvailableRepository emailAvailableRepository;

    private User testUser;
    private User userInfo;
    private String token;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        userInfo = User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .username("testuser")
                .address("testaddress")
                .phone("1234567890")
                .build();
        testUser = User.builder()
                .email(cryptoUtil.encrypt("test@example.com"))
                .password(passwordEncoder.encode("password"))
                .username(cryptoUtil.encrypt("testuser"))
                .address(cryptoUtil.encrypt("testaddress"))
                .phone(cryptoUtil.encrypt("1234567890"))
                .build();
        userRepository.save(testUser);
        token = jwtUtil.createToken(testUser.getId());
        tokenBlacklistRepository.save(new TokenBlacklist(token, testUser.getId()));
    }

    @Test
    public void testLogin() throws Exception {
        LoginDto loginDto = new LoginDto("test@example.com", "password");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    public void testLoginFail() throws Exception {
        LoginDto loginDto = new LoginDto("test@example.com", "wrongpassword");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(delete("/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        Optional<TokenBlacklist> tokenBlacklist = tokenBlacklistRepository.findById(token);
        assertTrue(tokenBlacklist.isEmpty());
    }

    @Test
    public void testGetMyPage() throws Exception {
        mockMvc.perform(get("/user/my")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userInfo.getEmail()))
                .andExpect(jsonPath("$.username").value(userInfo.getUsername()))
                .andExpect(jsonPath("$.address").value(userInfo.getAddress()))
                .andExpect(jsonPath("$.phone").value(userInfo.getPhone()));
    }

    @Test
    public void testCreateUser() throws Exception {
        String email = "new@example.com";
        emailAvailableRepository.save(new EmailAvailable("new@example.com"));
        UserRequestDto userDto = new UserRequestDto(email, "newpassword", "newaddress", "0987654321", "newuser");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
        assertTrue(userRepository.findByEmail(cryptoUtil.encrypt(email)).isPresent());
    }

    @Test
    public void testUpdateMyPage() throws Exception {
        String address = "newaddress";
        UserRequestDto userDto = new UserRequestDto(null, null, address, "0987654321", null);

        mockMvc.perform(patch("/user/my")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
        assertEquals(address, cryptoUtil.decrypt(userRepository.findByEmail(testUser.getEmail())
                .orElseThrow()
                .getAddress()));
    }

    @Test
    public void testUpdatePassword() throws Exception {
        UpdatePasswordDto passwordDto = new UpdatePasswordDto("newpassword", "password");

        String token2 = jwtUtil.createToken(testUser.getId());
        tokenBlacklistRepository.save(new TokenBlacklist(token2, testUser.getId()));

        mockMvc.perform(get("/user/my")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/user/my/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/user/my/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/user/my")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isForbidden());
    }
}

