package com.shoppoc.order.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

import java.util.Objects;
import java.util.UUID;

public final class OrderId {

    private final String value;

    private OrderId(String value) {
        this.value = value;
    }

    public static OrderId newId() {
        return new OrderId(UUID.randomUUID().toString());
    }

    public static OrderId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Order id must not be blank"));
        }
        return new OrderId(value.trim());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderId)) {
            return false;
        }
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
