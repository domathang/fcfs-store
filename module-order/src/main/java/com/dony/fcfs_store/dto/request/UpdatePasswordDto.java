package com.dony.fcfs_store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordDto {
    private String newPassword;
    private String oldPassword;
}
