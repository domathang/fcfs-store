package com.dony.client;

import com.dony.dto.PassportResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import reactor.core.publisher.Mono;

@Headers({ "Accept: application/json" })
public interface ApiClient {
    @RequestLine("GET /auth/passport/{id}")
    Mono<PassportResponse> getPassport(@Param("id") Integer id);
}
