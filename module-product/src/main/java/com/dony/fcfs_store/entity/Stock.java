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
    @Column(name = "product_id", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Setter
    private Integer stock;
}
