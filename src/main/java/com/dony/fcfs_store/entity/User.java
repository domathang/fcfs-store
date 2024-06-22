package com.dony.fcfs_store.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "User")
@Builder
@Getter
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
}
