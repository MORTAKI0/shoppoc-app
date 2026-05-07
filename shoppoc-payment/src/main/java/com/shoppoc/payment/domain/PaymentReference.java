package com.shoppoc.payment.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

import java.util.Objects;

public final class PaymentReference {

    private final String value;

    private PaymentReference(String value) {
        this.value = value;
    }

    public static PaymentReference of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Payment reference must not be blank"));
        }
        return new PaymentReference(value.trim());
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentReference)) {
            return false;
        }
        PaymentReference that = (PaymentReference) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
