package com.dony.fcfs_store.repository;

import com.dony.fcfs_store.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Integer> {
    List<OrderProduct> findByStatus(String status);
}
