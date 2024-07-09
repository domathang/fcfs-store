package com.dony.common.passport;

import com.dony.common.exception.CustomException;
import com.dony.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

public class PassportFilter extends OncePerRequestFilter {

    private final String passportKey;

    public PassportFilter(String passportKey) {
        this.passportKey = passportKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String passportToken = request.getHeader("X-Passport");

        if (passportToken != null) {
            Claims payload = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(passportToken)
                    .getPayload();

            UserContext userContext = new UserContext();
            userContext.setUserId(payload.get("userId", Integer.class));
            userContext.setEmail(payload.get("email", String.class));
            userContext.setAddress(payload.get("address", String.class));
            userContext.setPhone(payload.get("phone", String.class));
            userContext.setUsername(payload.get("username", String.class));
            UserContext.setCurrentUser(userContext);
        } else {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "passport 토큰이 없음");
        }

        filterChain.doFilter(request, response);
        UserContext.clear();
    }

    public SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(passportKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
