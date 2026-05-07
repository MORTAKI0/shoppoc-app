package com.shoppoc.catalog.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

import java.util.Locale;
import java.util.Objects;

public final class Sku {

    private final String value;

    private Sku(String value) {
        this.value = value;
    }

    public static Sku of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("SKU must not be blank"));
        }
        return new Sku(value.trim().toUpperCase(Locale.ROOT));
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Sku)) { return false; }
        Sku sku = (Sku) o;
        return Objects.equals(value, sku.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
