package com.dony.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class PassportResponse {
    private Integer id;
    private String email;
    private String username;
    private String address;
    private String phone;
}
