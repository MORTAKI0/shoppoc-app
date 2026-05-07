package com.shoppoc.shared.error;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(DomainError.notFound(message));
    }
}
