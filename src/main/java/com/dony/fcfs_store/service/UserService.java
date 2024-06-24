package com.dony.fcfs_store.service;

import com.dony.fcfs_store.entity.redis.TokenBlacklist;
import com.dony.fcfs_store.repository.redis.TokenBlacklistRepository;
import com.dony.fcfs_store.util.CryptoUtil;
import com.dony.fcfs_store.util.JwtUtil;
import com.dony.fcfs_store.dto.LoginDto;
import com.dony.fcfs_store.dto.TokenResponse;
import com.dony.fcfs_store.dto.UserRequestDto;
import com.dony.fcfs_store.dto.UserResponseDto;
import com.dony.fcfs_store.entity.User;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.redis.EmailAvailableRepository;
import com.dony.fcfs_store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAvailableRepository emailAvailableRepository;
    private final CryptoUtil cryptoUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;


    public TokenResponse login(LoginDto loginDto) {
        User user = userRepository.findByEmail(cryptoUtil.encrypt(loginDto.getEmail()))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 이메일로 가입한 유저가 존재하지 않음"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.NOT_VALID_PASSWORD);

        return new TokenResponse(jwtUtil.createToken(user.getId()));
    }

    public void logout(String token) {
        tokenBlacklistRepository.save(new TokenBlacklist(token));
    }

    public void createUser(UserRequestDto userDto) {
        emailAvailableRepository.findById(userDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_AUTHENTICATED));

        String encryptedPassword = passwordEncoder.encode(userDto.getPassword());
        String encryptedEmail = cryptoUtil.encrypt(userDto.getEmail());
        String encryptedAddress = cryptoUtil.encrypt(userDto.getAddress());
        String encryptedPhone = cryptoUtil.encrypt(userDto.getPhone());
        String encryptedUsername = cryptoUtil.encrypt(userDto.getUsername());

        User newUser = User.builder()
                .email(encryptedEmail)
                .username(encryptedUsername)
                .phone(encryptedPhone)
                .address(encryptedAddress)
                .password(encryptedPassword)
                .build();
        userRepository.save(newUser);
    }

    public UserResponseDto myPage(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow();

        // TODO

        return UserResponseDto.builder()
                .address(cryptoUtil.decrypt(user.getAddress()))
                .email(cryptoUtil.decrypt(user.getEmail()))
                .phone(cryptoUtil.decrypt(user.getPhone()))
                .username(cryptoUtil.decrypt(user.getUsername()))
                .build();
    }
}
