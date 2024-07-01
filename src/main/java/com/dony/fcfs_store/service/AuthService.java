package com.dony.fcfs_store.service;

import com.dony.fcfs_store.entity.redis.EmailAvailable;
import com.dony.fcfs_store.entity.redis.AuthCode;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.UserRepository;
import com.dony.fcfs_store.repository.redis.EmailAvailableRepository;
import com.dony.fcfs_store.repository.redis.AuthCodeRepository;
import com.dony.fcfs_store.util.CryptoUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JavaMailSender javaMailSender;
    private final AuthCodeRepository authCodeRepository;
    private final EmailAvailableRepository emailAvailableRepository;
    private final UserRepository userRepository;
    private final CryptoUtil cryptoUtil;

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
            String body = "Please click the link to verify your email: " + host + "/verify?token=" + rand;

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
}
