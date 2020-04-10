package com.techtest.computedashboardapi.model.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ResponseError {

    private HttpStatus status;
    private LocalDateTime timestamp;
    private String errorCode;
    private String errorDescription;

    public ResponseError() {
        timestamp = LocalDateTime.now();
    }

    public ResponseError(HttpStatus status, String errorCode, Throwable ex) {
        this();
        this.status = status;
        this.errorCode = errorCode;
        this.errorDescription = ex.getLocalizedMessage();
    }

}
