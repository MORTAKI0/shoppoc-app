package com.shoppoc.shared.error;

public final class DomainError {

    private final String code;
    private final String message;

    private DomainError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static DomainError of(String code, String message) {
        return new DomainError(code, message);
    }

    public static DomainError validation(String message) {
        return of("VALIDATION_ERROR", message);
    }

    public static DomainError notFound(String message) {
        return of("NOT_FOUND", message);
    }

    public static DomainError conflict(String message) {
        return of("CONFLICT", message);
    }

    public static DomainError unauthorized(String message) {
        return of("UNAUTHORIZED", message);
    }

    public static DomainError forbidden(String message) {
        return of("FORBIDDEN", message);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
