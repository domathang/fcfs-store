package com.dony.fcfs_store.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private String email;
    private String username;
    private String address;
    private String phone;
    private String imageUrl;
}
