package com.dony.fcfs_store.entity.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "authCode", timeToLive = 300)
public class AuthCode {
    @Id
    private String id;
    @Indexed
    private String code;
}
