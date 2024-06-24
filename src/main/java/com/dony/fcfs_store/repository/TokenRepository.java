package com.dony.fcfs_store.repository;

import com.dony.fcfs_store.entity.redis.Token;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, String> {
    Optional<Token> findByToken(String token);
}
