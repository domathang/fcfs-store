package com.dony.fcfs_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
