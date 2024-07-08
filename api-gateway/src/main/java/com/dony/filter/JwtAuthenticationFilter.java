package com.dony.filter;

import com.dony.client.ApiClient;
import com.dony.dto.PassportResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactivefeign.webclient.WebReactiveFeign;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${auth.jwt.secret}")
    private String authKey;

    @Value("${auth.jwt.passport.key}")
    private String passportKey;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // grab configuration from Config object
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            HttpHeaders headers = request.getHeaders();
            String authHeader = headers.getOrEmpty(HttpHeaders.AUTHORIZATION)
                    .getFirst();

            if (!authHeader.startsWith("Bearer ")) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            String token = authHeader.substring(7);

            try {
                Claims payload = Jwts.parser()
                        .verifyWith(getSecretKey(authKey))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                Integer userId = Integer.parseInt(payload.getSubject());

                ApiClient apiClient = WebReactiveFeign.<ApiClient>builder()
                        .target(ApiClient.class, "http://localhost:8080");

                Mono<PassportResponse> monoPassport = apiClient.getPassport(userId);

                return monoPassport.flatMap(passport -> {

                    //Generate new Passport Token
                    Claims claims = Jwts.claims()
                            .add("username", passport.getUsername())
                            .add("id", passport.getId())
                            .add("email", passport.getEmail())
                            .add("address", passport.getAddress())
                            .add("phone", passport.getPhone())
                            .build();

                    String passportToken = Jwts.builder()
                            .claims(claims)
                            .signWith(getSecretKey(passportKey))
                            .compact();

                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-Passport", passportToken)
                            .build();

                    log.info(modifiedRequest.getHeaders().get("X-Passport").get(0));

                    //use builder to manipulate the request
                    return chain.filter(exchange.mutate().request(modifiedRequest).build())
                            .then(Mono.fromRunnable(() -> {
                                log.info("Custom POST filter: response status code -> {}", response.getStatusCode());
                            }));
                });
            } catch (Exception e) {
                log.error("JWT validation failed", e);
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        };
    }


    private SecretKey getSecretKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static class Config {
        //Put the configuration properties for your filter here
    }
}
