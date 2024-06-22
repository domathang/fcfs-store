package com.dony.fcfs_store.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getDetail());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(e.getErrorCode()
                .getHttpStatus()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.SERVER_ERROR, ErrorCode.SERVER_ERROR.getExplain());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(500));
    }
}
