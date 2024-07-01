package com.dony.fcfs_store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Cart_product")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Setter
    private Integer quantity;

}
