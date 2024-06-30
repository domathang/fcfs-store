package com.dony.fcfs_store.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "image_url")
    private String imageUrl;

    @Setter
    private Integer stock;

    @CreatedDate
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String detail;

    @Column(name = "sale_start_time")
    private LocalDateTime saleStartTime;

//    @OneToOne(mappedBy = "product")
//    private Stock stock;

    @OneToMany(mappedBy = "product")
    private List<OrderProduct> orderProducts;

    @OneToMany(mappedBy = "product")
    private List<CartProduct> cartProducts;
}
