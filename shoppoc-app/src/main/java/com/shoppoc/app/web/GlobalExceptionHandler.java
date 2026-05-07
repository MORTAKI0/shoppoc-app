package com.shoppoc.app.web;

import com.shoppoc.shared.error.ApiErrorResponse;
import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;
import com.shoppoc.shared.error.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                         HttpServletRequest request) {
        List<String> details = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", request.getRequestURI(), details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                      HttpServletRequest request) {
        List<String> details = new ArrayList<String>();
        ex.getConstraintViolations().forEach(v -> details.add(v.getPropertyPath() + ": " + v.getMessage()));
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", request.getRequestURI(), details);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex,
                                                           HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request.getRequestURI(), Collections.<String>emptyList());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex,
                                                                    HttpServletRequest request) {
        DomainError domainError = ex.getDomainError();
        String code = domainError != null ? domainError.getCode() : "BUSINESS_ERROR";
        String message = domainError != null ? domainError.getMessage() : "Business rule violation";
        return buildResponse(HttpStatus.CONFLICT, code, message, request.getRequestURI(), Collections.<String>emptyList());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex,
                                                                          HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication required", request.getRequestURI(), Collections.<String>emptyList());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
                                                                        HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", "Access denied", request.getRequestURI(), Collections.<String>emptyList());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex,
                                                                   HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error occurred", request.getRequestURI(), Collections.<String>emptyList());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status,
                                                           String error,
                                                           String message,
                                                           String path,
                                                           List<String> details) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                path,
                details
        );
        return ResponseEntity.status(status).body(response);
    }
}
