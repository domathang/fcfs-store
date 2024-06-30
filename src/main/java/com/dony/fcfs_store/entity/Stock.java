package com.dony.fcfs_store.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Stock")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    private Integer stock;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
