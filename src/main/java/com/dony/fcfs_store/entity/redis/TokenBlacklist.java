package com.dony.fcfs_store.entity.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "blacklist", timeToLive = 86400)
public class TokenBlacklist {
    @Id
    private String id;
    @Indexed
    Integer userId;
    @Setter
    private Boolean available;
}