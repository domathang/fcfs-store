package com.dony.fcfs_store.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @Email
    private String email;
    @NotNull
    private String username;
    @NotNull
    private String address;
    @NotNull
    private String phone;
    @NotNull
    private String password;
}
