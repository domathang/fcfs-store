package com.dony.fcfs_store.repository;

import com.dony.fcfs_store.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
