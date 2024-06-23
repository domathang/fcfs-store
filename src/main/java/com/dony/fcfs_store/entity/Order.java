package com.dony.fcfs_store.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    private String status;

    @Column(columnDefinition = "TINYINT(1) default 0")
    private Boolean isDeliveryCompleted;

    private LocalDateTime deliveryCompletedAt;

    @Column(columnDefinition = "TINYINT(1) default 0")
    private Boolean isReturnCompleted;

    private LocalDateTime returnCompletedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderProduct> orderProducts;
}
