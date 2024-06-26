package com.dony.fcfs_store.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    @Email
    private String email;
    private String username;
    private String address;
    private String phone;
    private String password;
}
