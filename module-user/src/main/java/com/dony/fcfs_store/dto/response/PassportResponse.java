package com.dony.fcfs_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class PassportResponse {
    private Integer id;
    private String email;
    private String address;
    private String phone;
}
