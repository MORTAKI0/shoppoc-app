package com.shoppoc.payment.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

import java.util.Objects;
import java.util.UUID;

public final class PaymentId {

    private final UUID value;

    private PaymentId(UUID value) {
        this.value = value;
    }

    public static PaymentId newId() {
        return new PaymentId(UUID.randomUUID());
    }

    public static PaymentId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Payment id must not be blank"));
        }
        try {
            return new PaymentId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(DomainError.validation("Invalid payment id format"));
        }
    }

    public String value() {
        return value.toString();
    }

    @Override
    public String toString() {
        return value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentId)) {
            return false;
        }
        PaymentId paymentId = (PaymentId) o;
        return Objects.equals(value, paymentId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
