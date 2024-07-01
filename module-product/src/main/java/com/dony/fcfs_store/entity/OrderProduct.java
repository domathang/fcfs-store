package com.dony.fcfs_store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Order_product")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    private Integer totalPrice;

    @Setter
    private String status;

    @Setter
    private LocalDateTime returnAcceptedAt;

    @Setter
    private LocalDateTime returnCompletedAt;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
