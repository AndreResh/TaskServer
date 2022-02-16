package com.example.taskserver.exeption;

public class ApiRequestExceptions extends RuntimeException {
    public ApiRequestExceptions(String message) {
        super(message);
    }

    public ApiRequestExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
