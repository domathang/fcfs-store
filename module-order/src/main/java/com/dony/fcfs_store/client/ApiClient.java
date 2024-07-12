package com.dony.fcfs_store.client;

import com.dony.fcfs_store.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "api-client", url = "localhost:8081")
public interface ApiClient {
    @GetMapping("/internal/api/product/{id}")
    ProductResponse getProduct(@PathVariable Integer id);
}
