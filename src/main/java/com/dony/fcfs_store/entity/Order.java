package com.dony.fcfs_store.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`Order`")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreatedDate
    private LocalDateTime orderedAt;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Setter
    private String status;

    @Setter
    private LocalDateTime deliveryCompletedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderProduct> orderProducts;
}
