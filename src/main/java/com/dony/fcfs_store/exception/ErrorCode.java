package com.dony.fcfs_store.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SERVER_ERROR(500, "서버 에러");

    private final Integer httpStatus;
    private final String explain;

    ErrorCode(Integer httpStatus, String explain) {
        this.httpStatus = httpStatus;
        this.explain = explain;
    }
}
