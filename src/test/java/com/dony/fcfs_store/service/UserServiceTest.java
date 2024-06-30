package com.dony.fcfs_store.service;

import com.dony.fcfs_store.dto.request.LoginDto;
import com.dony.fcfs_store.dto.request.UpdatePasswordDto;
import com.dony.fcfs_store.dto.request.UserRequestDto;
import com.dony.fcfs_store.dto.response.TokenResponse;
import com.dony.fcfs_store.dto.response.UserResponseDto;
import com.dony.fcfs_store.entity.User;
import com.dony.fcfs_store.entity.redis.EmailAvailable;
import com.dony.fcfs_store.entity.redis.TokenBlacklist;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.UserRepository;
import com.dony.fcfs_store.repository.redis.EmailAvailableRepository;
import com.dony.fcfs_store.repository.redis.TokenBlacklistRepository;
import com.dony.fcfs_store.util.AuthenticationFacade;
import com.dony.fcfs_store.util.CryptoUtil;
import com.dony.fcfs_store.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailAvailableRepository emailAvailableRepository;

    @MockBean
    private CryptoUtil cryptoUtil;

    @MockBean
    private TokenBlacklistRepository tokenBlacklistRepository;

    @MockBean
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1)
                .email("encryptedEmail")
                .username("encryptedUsername")
                .phone("encryptedPhone")
                .address("encryptedAddress")
                .password("encryptedPassword")
                .build();
    }

    @Test
    public void testLogin_Success() {
        LoginDto loginDto = new LoginDto("email@example.com", "password");

        when(cryptoUtil.encrypt(loginDto.getEmail())).thenReturn("encryptedEmail");
        when(userRepository.findByEmail("encryptedEmail")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.createToken(user.getId())).thenReturn("jwtToken");

        TokenResponse response = userService.login(loginDto);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        verify(tokenBlacklistRepository, times(1)).save(any(TokenBlacklist.class));
    }

    @Test
    public void testLogin_InvalidPassword() {
        LoginDto loginDto = new LoginDto("email@example.com", "invalidPassword");

//        when(cryptoUtil.encrypt(loginDto.getEmail())).thenReturn("encryptedEmail");
        when(userRepository.findByEmail("encryptedEmail")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> userService.login(loginDto));
        assertEquals(ErrorCode.NOT_VALID_PASSWORD, exception.getErrorCode());
    }

    @Test
    public void testLogout_Success() {
        String token = "jwtToken";
        TokenBlacklist blacklist = new TokenBlacklist(token, 1, true);

        when(tokenBlacklistRepository.findById(token)).thenReturn(Optional.of(blacklist));

        userService.logout(token);

        assertFalse(blacklist.getAvailable());
        verify(tokenBlacklistRepository, times(1)).findById(token);
    }

    @Test
    public void testCreateUser_Success() {
        UserRequestDto userRequestDto = new UserRequestDto("email@example.com", "password", "username", "address", "phone");

        when(emailAvailableRepository.findById(userRequestDto.getEmail())).thenReturn(Optional.of(new EmailAvailable("email@example.com")));
//        when(cryptoUtil.encrypt(userRequestDto.getEmail())).thenReturn("encryptedEmail");
//        when(cryptoUtil.encrypt(userRequestDto.getUsername())).thenReturn("encryptedUsername");
//        when(cryptoUtil.encrypt(userRequestDto.getAddress())).thenReturn("encryptedAddress");
//        when(cryptoUtil.encrypt(userRequestDto.getPhone())).thenReturn("encryptedPhone");
//        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("encryptedPassword");

        userService.createUser(userRequestDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testMyPage_Success() {
        when(authenticationFacade.getLoggedInUser()).thenReturn(user);
//        when(cryptoUtil.decrypt(user.getEmail())).thenReturn("decryptedEmail");
//        when(cryptoUtil.decrypt(user.getUsername())).thenReturn("decryptedUsername");
//        when(cryptoUtil.decrypt(user.getAddress())).thenReturn("decryptedAddress");
//        when(cryptoUtil.decrypt(user.getPhone())).thenReturn("decryptedPhone");

        UserResponseDto response = userService.myPage();

        assertNotNull(response);
        assertEquals("decryptedEmail", response.getEmail());
        assertEquals("decryptedUsername", response.getUsername());
        assertEquals("decryptedAddress", response.getAddress());
        assertEquals("decryptedPhone", response.getPhone());
    }

    @Test
    public void testUpdateMyPage_Success() {
        UserRequestDto userRequestDto = new UserRequestDto("email@example.com", null, "username", "newAddress", "newPhone");
        when(authenticationFacade.getLoggedInUser()).thenReturn(user);
//        when(cryptoUtil.encrypt(userRequestDto.getAddress())).thenReturn("encryptedNewAddress");
//        when(cryptoUtil.encrypt(userRequestDto.getPhone())).thenReturn("encryptedNewPhone");

        userService.updateMyPage(userRequestDto);

        assertEquals("encryptedNewAddress", user.getAddress());
        assertEquals("encryptedNewPhone", user.getPhone());
    }

    @Test
    public void testUpdatePassword_Success() {
        UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto("newPassword", "oldPassword");
        when(authenticationFacade.getLoggedInUser()).thenReturn(user);
        when(passwordEncoder.matches(updatePasswordDto.getOldPassword(), user.getPassword())).thenReturn(true);

        userService.updatePassword(updatePasswordDto);

        assertEquals("newPassword", user.getPassword());
        verify(tokenBlacklistRepository, times(1)).findAllByUserId(user.getId());
    }
}
