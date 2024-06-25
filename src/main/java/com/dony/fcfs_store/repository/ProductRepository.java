package com.dony.fcfs_store.repository;

import com.dony.fcfs_store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByOwnerId(Integer ownerId);
}
