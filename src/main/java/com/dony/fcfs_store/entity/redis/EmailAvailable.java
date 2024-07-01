package com.dony.fcfs_store.entity.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "EmailAvailable", timeToLive = 300)
public class EmailAvailable {
    @Id
    private String id;
}
