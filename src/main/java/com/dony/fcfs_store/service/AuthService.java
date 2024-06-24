package com.dony.fcfs_store.service;

import com.dony.fcfs_store.entity.redis.EmailAvailable;
import com.dony.fcfs_store.entity.redis.Token;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.EmailAvailableRepository;
import com.dony.fcfs_store.repository.TokenRepository;
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
    private final TokenRepository tokenRepository;
    private final EmailAvailableRepository emailAvailableRepository;

    @Value("${spring.mail.content.host}")
    private String host;

    public void sendEmail(String to) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);

            String rand = UUID.randomUUID()
                    .toString()
                    .replaceAll("-", "")
                    .substring(0, 6);

            String subject = "이메일 인증";
            String body = "Please click the link to verify your email: " + host + "/verify?token=" + rand;
            helper.setSubject(subject);
            helper.setText(body);

            javaMailSender.send(message);

            tokenRepository.save(new Token(to, rand));
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.MESSAGING_ERROR);
        }
    }

    public void verify(String authToken) {
        Token token = tokenRepository.findByToken(authToken)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        emailAvailableRepository.save(new EmailAvailable(token.getId()));
    }
}
