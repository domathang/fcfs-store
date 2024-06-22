package com.dony.fcfs_store.service;

import com.dony.fcfs_store.dto.UserRequestDto;
import com.dony.fcfs_store.dto.UserResponseDto;
import com.dony.fcfs_store.entity.User;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.UserRepository;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

// TODO: 암호화/복호화 하는 서비스 분리
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String ALGORITHM = "AES";

    @Value("${auth.symmetric.key}")
    private String symmetricKey;

    public void createUser(UserRequestDto userDto) {
        // TODO: email 인증 확인

        String encryptedPassword = passwordEncoder.encode(userDto.getPassword());
        String encryptedEmail = encryptPersonalInfo(userDto.getEmail());
        String encryptedAddress = encryptPersonalInfo(userDto.getAddress());
        String encryptedPhone = encryptPersonalInfo(userDto.getPhone());
        String encryptedUsername = encryptPersonalInfo(userDto.getUsername());

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
                .address(decryptPersonalInfo(user.getAddress()))
                .email(decryptPersonalInfo(user.getEmail()))
                .phone(decryptPersonalInfo(user.getPhone()))
                .username(decryptPersonalInfo(user.getUsername()))
                .build();
    }

    public SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(symmetricKey);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    private String encryptPersonalInfo(String field) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] bytes = cipher.doFinal(field.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR, "개인정보 암호화 중 에러");
        }
    }

    private String decryptPersonalInfo(String field) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] decodedBytes = Base64.getDecoder()
                    .decode(field);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR, "개인정보 복호화 중 에러");
        }
    }
}
