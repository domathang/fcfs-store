package com.dony.fcfs_store.service;

import com.dony.fcfs_store.dto.request.LoginDto;
import com.dony.fcfs_store.dto.response.PassportResponse;
import com.dony.fcfs_store.dto.response.TokenDto;
import com.dony.fcfs_store.entity.User;
import com.dony.fcfs_store.entity.redis.AuthCode;
import com.dony.fcfs_store.entity.redis.EmailAvailable;
import com.dony.fcfs_store.entity.redis.TokenBlacklist;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.UserRepository;
import com.dony.fcfs_store.repository.redis.AuthCodeRepository;
import com.dony.fcfs_store.repository.redis.EmailAvailableRepository;
import com.dony.fcfs_store.repository.redis.TokenBlacklistRepository;
import com.dony.fcfs_store.util.CryptoUtil;
import com.dony.fcfs_store.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JavaMailSender javaMailSender;

    private final AuthCodeRepository authCodeRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final EmailAvailableRepository emailAvailableRepository;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;
    private final CryptoUtil cryptoUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.content.host}")
    private String host;

    public void sendEmail(String to) {
        if (userRepository.findByEmail(cryptoUtil.encrypt(to)).isPresent())
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);


            String rand = UUID.randomUUID()
                    .toString()
                    .replaceAll("-", "")
                    .substring(0, 6);

            String subject = "이메일 인증";
            String body = "Please click the link to verify your email: " + host + "/auth/verify?token=" + rand;

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            javaMailSender.send(message);

            authCodeRepository.save(new AuthCode(to, rand));
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.MESSAGING_ERROR);
        }
    }

    public void verify(String authCode) {
        AuthCode code = authCodeRepository.findByCode(authCode)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        emailAvailableRepository.save(new EmailAvailable(code.getId()));
    }

    @Transactional
    public TokenDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(cryptoUtil.encrypt(loginDto.getEmail()))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 이메일로 가입한 유저가 존재하지 않음"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.NOT_VALID_PASSWORD);

        String token = jwtUtil.createToken(user.getId());

        tokenBlacklistRepository.save(new TokenBlacklist(token, user.getId()));

        return new TokenDto(token);
    }

    @Transactional
    public void logout(String token) {
        token = jwtUtil.resolveToken(token);
        tokenBlacklistRepository.deleteById(token);
    }

    public PassportResponse getUserPassportByAccessToken(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        return PassportResponse.builder()
                .id(id)
                .username(cryptoUtil.decrypt(user.getUsername()))
                .email(cryptoUtil.decrypt(user.getEmail()))
                .address(cryptoUtil.decrypt(user.getAddress()))
                .phone(cryptoUtil.decrypt(user.getPhone()))
                .build();
    }
}
