package com.dony.fcfs_store.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class CartOrderRequestDto {
    private List<Integer> productIdList;
}
