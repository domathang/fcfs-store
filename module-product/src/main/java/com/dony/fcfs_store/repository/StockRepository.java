package com.dony.fcfs_store.repository;

import com.dony.fcfs_store.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Integer> {
}
