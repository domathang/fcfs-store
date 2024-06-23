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
@Table(name = "Product")
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer price;

    private String image_url;

    private Integer stock;

    @CreatedDate
    private LocalDateTime registeredAt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String detail;

    private LocalDateTime saleStartTime;

    @OneToMany(mappedBy = "product")
    private List<OrderProduct> orderProducts;

    @OneToMany(mappedBy = "product")
    private List<CartProduct> cartProducts;
}
