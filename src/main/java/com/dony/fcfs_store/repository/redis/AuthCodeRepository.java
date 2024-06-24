package com.dony.fcfs_store.repository.redis;

import com.dony.fcfs_store.entity.redis.AuthCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthCodeRepository extends CrudRepository<AuthCode, String> {
    Optional<AuthCode> findByCode(String code);
}
