package com.mthree.mbank.controller;

import lombok.Getter;

// Inner class for API responses to maintain a consistent response format
@Getter
public class ApiResponse {
    private final String message;
    private final Object data;

    public ApiResponse(String message) {
        this(message, null);
    }

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

}
