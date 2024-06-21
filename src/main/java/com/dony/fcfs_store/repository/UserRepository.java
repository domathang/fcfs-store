package com.dony.fcfs_store.repository;

import com.dony.fcfs_store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
