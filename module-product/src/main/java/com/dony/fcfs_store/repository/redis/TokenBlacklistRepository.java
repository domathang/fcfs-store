package com.dony.fcfs_store.repository.redis;

import com.dony.fcfs_store.entity.redis.TokenBlacklist;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface TokenBlacklistRepository extends CrudRepository<TokenBlacklist, String> {
   List<TokenBlacklist> findAllByUserId(Integer userId);
}
