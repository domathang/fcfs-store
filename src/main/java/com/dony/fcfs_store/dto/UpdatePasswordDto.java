package com.dony.fcfs_store.dto;

import lombok.Getter;

@Getter
public class UpdatePasswordDto {
    private String newPassword;
    private String oldPassword;
}
