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
@Table(name = "User")
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private String password;

    private String username;

    private String address;

    private String phone;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private List<Order> orders;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.PERSIST)
    private List<Product> products;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<CartProduct> cartProducts;
}
