package com.dony.fcfs_store.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private String reason;
}
