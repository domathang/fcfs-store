package com.dony.fcfs_store.repository;

import com.dony.fcfs_store.entity.redis.EmailAvailable;
import org.springframework.data.repository.CrudRepository;

public interface EmailAvailableRepository extends CrudRepository<EmailAvailable, String> {
}
