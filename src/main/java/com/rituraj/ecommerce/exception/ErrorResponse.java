package com.rituraj.ecommerce.exception;

import org.springframework.http.HttpStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;

    public ErrorResponse(HttpStatus status, String message) {
        this.timeStamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
    }
}