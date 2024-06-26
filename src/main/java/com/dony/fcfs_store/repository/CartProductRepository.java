package com.dony.fcfs_store.repository;

import com.dony.fcfs_store.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Integer> {
    List<CartProduct> findByCustomerId(Integer customerId);
}
