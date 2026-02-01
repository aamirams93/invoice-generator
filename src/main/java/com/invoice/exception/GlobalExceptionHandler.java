package com.invoice.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        String traceId = UUID.randomUUID().toString();

        log.warn("BusinessException occurred traceId={}, path={}, errorCode={}, message={}",
                traceId, request.getRequestURI(), ex.getErrorCode(), ex.getMessage());

        ApiError error = new ApiError(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String traceId = UUID.randomUUID().toString();

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        log.debug("Validation failure traceId={}, path={}, error={}", traceId, request.getRequestURI(), message);

        ApiError error = new ApiError(
                "VALIDATION_ERROR",
                message,
                request.getRequestURI(),
                Instant.now(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request) {

        String traceId = UUID.randomUUID().toString();

        log.info("ResponseStatusException traceId={}, path={}, status={}, reason={}",
                traceId, request.getRequestURI(), ex.getStatusCode(), ex.getReason());

        ApiError error = new ApiError(
                ex.getStatusCode().toString(),
                ex.getReason(),
                request.getRequestURI(),
                Instant.now(),
                traceId
        );

        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        String traceId = UUID.randomUUID().toString();

        log.error("Unhandled exception traceId={}, path={}, message={}", traceId, request.getRequestURI(), ex.getMessage(), ex);

        ApiError error = new ApiError(
                "INTERNAL_SERVER_ERROR",
                "Something went wrong. Please contact support.",
                request.getRequestURI(),
                Instant.now(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
