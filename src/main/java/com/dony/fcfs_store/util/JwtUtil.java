package com.dony.fcfs_store.util;

import com.dony.fcfs_store.entity.User;
import com.dony.fcfs_store.exception.CustomException;
import com.dony.fcfs_store.exception.ErrorCode;
import com.dony.fcfs_store.repository.UserRepository;
import com.dony.fcfs_store.repository.redis.TokenBlacklistRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${auth.jwt.secret}")
    private String keyStr;

    @Value("${auth.jwt.expiration_time}")
    private Long accessTokenLifespan;

    private final UserRepository userRepository;

    private final TokenBlacklistRepository tokenBlacklistRepository;

    public SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(keyStr);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Integer id) {
        Claims claims = Jwts.claims()
                .subject(id.toString())
                .expiration(new Date(System.currentTimeMillis() + accessTokenLifespan))
                .issuedAt(new Date())
                .build();

        return Jwts.builder()
                .claims(claims)
                .signWith(getSecretKey())
                .compact();
    }

    public Authentication getAuthentication(String token) {
        if (tokenBlacklistRepository.findById(token).isPresent())
            throw new CustomException(ErrorCode.UNAUTHORIZED, "다시 로그인해서 올바른 토큰을 받아야함");
        Integer id = getId(token);
        Optional<User> user = userRepository.findById(id);
        return user.map(value -> new UsernamePasswordAuthenticationToken(user, "", getAuthorities()))
                .orElseGet(() -> new UsernamePasswordAuthenticationToken(null, "", null));
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }

    public Integer getId(String token) {
        Claims payload = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Integer.parseInt(payload.getSubject());
    }

    public String resolveToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) return null;
        else return token.substring(7);
    }

}
