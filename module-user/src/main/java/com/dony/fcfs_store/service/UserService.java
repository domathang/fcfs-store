package com.dony.fcfs_store.service;

import com.dony.fcfs_store.dto.request.LoginDto;
import com.dony.fcfs_store.dto.request.UpdatePasswordDto;
import com.dony.fcfs_store.dto.request.UserRequestDto;
import com.dony.fcfs_store.dto.response.TokenResponse;
import com.dony.fcfs_store.dto.response.UserResponseDto;
import com.dony.fcfs_store.entity.User;
import com.dony.fcfs_store.entity.redis.TokenBlacklist;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.UserRepository;
import com.dony.fcfs_store.repository.redis.EmailAvailableRepository;
import com.dony.fcfs_store.repository.redis.TokenBlacklistRepository;
import com.dony.fcfs_store.util.AuthenticationFacade;
import com.dony.fcfs_store.util.CryptoUtil;
import com.dony.fcfs_store.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAvailableRepository emailAvailableRepository;
    private final CryptoUtil cryptoUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final AuthenticationFacade authenticationFacade;


    @Transactional
    public TokenResponse login(LoginDto loginDto) {
        User user = userRepository.findByEmail(cryptoUtil.encrypt(loginDto.getEmail()))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 이메일로 가입한 유저가 존재하지 않음"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.NOT_VALID_PASSWORD);

        String token = jwtUtil.createToken(user.getId());

        tokenBlacklistRepository.save(new TokenBlacklist(token, user.getId()));

        return new TokenResponse(token);
    }

    @Transactional
    public void logout(String token) {
        token = jwtUtil.resolveToken(token);
        tokenBlacklistRepository.deleteById(token);
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

    public UserResponseDto myPage() {
        User user = authenticationFacade.getLoggedInUser();

        return UserResponseDto.builder()
                .address(cryptoUtil.decrypt(user.getAddress()))
                .email(cryptoUtil.decrypt(user.getEmail()))
                .phone(cryptoUtil.decrypt(user.getPhone()))
                .username(cryptoUtil.decrypt(user.getUsername()))
                .imageUrl(user.getImageUrl())
                .build();
    }

    @Transactional
    public void updateMyPage(UserRequestDto dto) {
        User user = authenticationFacade.getLoggedInUser();
        if (dto.getAddress() != null)
            user.setAddress(cryptoUtil.encrypt(dto.getAddress()));
        if (dto.getPhone() != null)
            user.setPhone(cryptoUtil.encrypt(dto.getPhone()));
    }

    @Transactional
    public void updatePassword(UpdatePasswordDto dto) {
        User user = authenticationFacade.getLoggedInUser();
        if (passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            user.setPassword(dto.getNewPassword());
            List<TokenBlacklist> tokens = tokenBlacklistRepository.findAllByUserId(user.getId());
            tokenBlacklistRepository.deleteAll(tokens);
        }
        else {
            throw new CustomException(ErrorCode.NOT_VALID_PASSWORD);
        }
    }
}
