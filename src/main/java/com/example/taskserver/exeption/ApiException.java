package com.example.taskserver.exeption;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiException {
//    private final Throwable throwable;
    private final String message;
    private final HttpStatus status;
    private final LocalDateTime time;
}
