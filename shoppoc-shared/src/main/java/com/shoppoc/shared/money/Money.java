package com.shoppoc.shared.money;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money {

    private static final int DEFAULT_SCALE = 2;

    private final BigDecimal amount;
    private final String currency;

    private Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new BusinessException(DomainError.validation("Amount must not be null"));
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Currency must not be blank"));
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(DomainError.validation("Amount must not be negative"));
        }

        BigDecimal normalized = amount.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        return new Money(normalized, currency.trim().toUpperCase());
    }

    public static Money zero(String currency) {
        return of(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        if (other == null) {
            throw new BusinessException(DomainError.validation("Money to add must not be null"));
        }
        if (!currency.equals(other.currency)) {
            throw new BusinessException(DomainError.conflict("Cannot add money with different currencies"));
        }
        return Money.of(amount.add(other.amount), currency);
    }

    public Money multiply(int quantity) {
        if (quantity < 0) {
            throw new BusinessException(DomainError.validation("Quantity must not be negative"));
        }
        return Money.of(amount.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money)) {
            return false;
        }
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return "Money{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
    }
}
