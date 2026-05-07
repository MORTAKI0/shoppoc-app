package com.shoppoc.user.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

public final class PasswordHash {

    private final String value;

    private PasswordHash(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Password hash must not be blank"));
        }
        this.value = value;
    }

    public static PasswordHash of(String value) {
        return new PasswordHash(value);
    }

    public String getValue() {
        return value;
    }
}
