package com.dony.fcfs_store.repository.redis;

import com.dony.fcfs_store.entity.redis.TokenBlacklist;
import org.springframework.data.repository.CrudRepository;

public interface TokenBlacklistRepository extends CrudRepository<TokenBlacklist, String> {
}
