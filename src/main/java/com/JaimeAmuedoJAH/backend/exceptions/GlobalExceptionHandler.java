package com.JaimeAmuedoJAH.backend.exceptions;

import com.JaimeAmuedoJAH.backend.service.ErrorTrackingService;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired(required = false)
    private ErrorTrackingService errorTrackingService;

    @Autowired(required = false)
    private HttpServletRequest request;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex) {
        Map<String, Object> error = createErrorBody(ex.getStatus(), ex.getMessage());
        error.put("error", ex.getStatus().getReasonPhrase());
        
        // Track error if service is available
        if (errorTrackingService != null && request != null) {
            errorTrackingService.trackError(
                    ex.getStatus(),
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    request,
                    getCurrentUserEmail(),
                    ex
            );
        }
        
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> error = createErrorBody(HttpStatus.BAD_REQUEST, "Validation failed");
        List<Map<String, String>> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        error.put("details", details);
        
        // Track error if service is available
        if (errorTrackingService != null && request != null) {
            errorTrackingService.trackError(
                    HttpStatus.BAD_REQUEST,
                    "MethodArgumentNotValidException",
                    "Validation failed",
                    request,
                    getCurrentUserEmail(),
                    ex
            );
        }
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> error = createErrorBody(HttpStatus.BAD_REQUEST, "Validation failed");
        List<Map<String, String>> details = ex.getConstraintViolations().stream()
                .map(violation -> Map.of(
                        "path", violation.getPropertyPath().toString(),
                        "message", violation.getMessage()))
                .collect(Collectors.toList());
        error.put("details", details);
        
        // Track error if service is available
        if (errorTrackingService != null && request != null) {
            errorTrackingService.trackError(
                    HttpStatus.BAD_REQUEST,
                    "ConstraintViolationException",
                    "Validation failed",
                    request,
                    getCurrentUserEmail(),
                    ex
            );
        }
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> error = createErrorBody(HttpStatus.BAD_REQUEST, "Malformed JSON request");
        
        // Track error if service is available
        if (errorTrackingService != null && request != null) {
            errorTrackingService.trackError(
                    HttpStatus.BAD_REQUEST,
                    "HttpMessageNotReadableException",
                    "Malformed JSON request",
                    request,
                    getCurrentUserEmail(),
                    ex
            );
        }
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        Map<String, Object> error = createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        error.put("error", "Internal Server Error");
        
        // Track error if service is available
        if (errorTrackingService != null && request != null) {
            errorTrackingService.trackError(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    request,
                    getCurrentUserEmail(),
                    ex
            );
        }
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> createErrorBody(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("message", message);
        return error;
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "ANONYMOUS";
    }
}
