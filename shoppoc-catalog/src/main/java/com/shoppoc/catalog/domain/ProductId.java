package com.shoppoc.catalog.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

import java.util.Objects;
import java.util.UUID;

public final class ProductId {

    private final UUID value;

    private ProductId(UUID value) {
        this.value = value;
    }

    public static ProductId of(UUID value) {
        if (value == null) {
            throw new BusinessException(DomainError.validation("ProductId must not be null"));
        }
        return new ProductId(value);
    }

    public static ProductId fromString(String value) {
        try {
            return of(UUID.fromString(value));
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(DomainError.validation("Invalid product id format"));
        }
    }

    public static ProductId newId() {
        return of(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ProductId)) { return false; }
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
