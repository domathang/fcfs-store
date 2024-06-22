package com.dony.fcfs_store.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    private String email;
    private String username;
    private String address;
    private String phone;
    private String password;
}
