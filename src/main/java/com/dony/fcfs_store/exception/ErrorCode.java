package com.dony.fcfs_store.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    EMAIL_NOT_AUTHENTICATED(400, "이메일이 인증되지 않음"),
    MESSAGING_ERROR(500, "인증 이메일 전송 중 오류 발생"),
    NOT_FOUND(404, "DB에 해당 정보의 row가 존재하지 않음"),
    SERVER_ERROR(500, "서버 에러");

    private final Integer httpStatus;
    private final String explain;

    ErrorCode(Integer httpStatus, String explain) {
        this.httpStatus = httpStatus;
        this.explain = explain;
    }
}
