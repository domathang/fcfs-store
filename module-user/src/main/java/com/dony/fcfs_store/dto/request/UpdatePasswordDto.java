package com.dony.fcfs_store.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordDto {
    @NotNull
    private String newPassword;
    @NotNull
    private String oldPassword;
}
