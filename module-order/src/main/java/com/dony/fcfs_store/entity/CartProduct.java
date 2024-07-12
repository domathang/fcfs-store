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

    private Integer customerId;

    private Integer productId;

    @Setter
    private Integer quantity;

}
