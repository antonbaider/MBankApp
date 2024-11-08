package com.mthree.mbank.exception;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Setter
@Getter
public class CustomErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private String errorCode;
    private Map<String, String> details;

    public CustomErrorResponse(int status, String error, String message, String errorCode, LocalDateTime timestamp, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
        this.path = path;
    }
}