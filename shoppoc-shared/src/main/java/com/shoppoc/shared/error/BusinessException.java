package com.shoppoc.shared.error;

public class BusinessException extends RuntimeException {

    private final DomainError domainError;

    public BusinessException(DomainError domainError) {
        super(domainError != null ? domainError.getMessage() : null);
        this.domainError = domainError;
    }

    public DomainError getDomainError() {
        return domainError;
    }
}
