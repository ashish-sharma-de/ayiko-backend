package com.ayiko.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public class ExceptionHandler {

    public static final String ERROR_INVALID_TOKEN = "Token is empty or invalid, valid token required to access this API";
    public static final String ERROR_DUPLICATE_EMAIL = "Email already present.";
    public static final String ERROR_INVALID_EMAIL = "Email invalid.";
    public static final String ERROR_INVALID_ID = "Specified supplier id is invalid";

    public static final String ERROR_INVALID_USERNAME = "Username invalid.";

    public static ResponseEntity handleException(Exception e) {
        if (e instanceof RuntimeException && (e.getMessage().equals(ERROR_INVALID_TOKEN) || e.getMessage().equals(ERROR_INVALID_ID))) {
            return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage())).build();
        }
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())).build();
    }
}
