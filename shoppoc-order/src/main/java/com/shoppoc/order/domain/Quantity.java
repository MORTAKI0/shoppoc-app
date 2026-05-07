package com.shoppoc.order.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

public final class Quantity {

    private final int value;

    private Quantity(int value) {
        this.value = value;
    }

    public static Quantity of(int value) {
        if (value <= 0) {
            throw new BusinessException(DomainError.validation("Quantity must be greater than zero"));
        }
        return new Quantity(value);
    }

    public int value() {
        return value;
    }
}
