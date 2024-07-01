package com.dony.fcfs_store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartOrderRequestDto {
    private List<Integer> cartProductIdList;
}
