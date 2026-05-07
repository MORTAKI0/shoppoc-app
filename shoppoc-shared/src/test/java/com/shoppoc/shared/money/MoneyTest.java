package com.shoppoc.shared.money;

import com.shoppoc.shared.error.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @Test
    void shouldCreateMoneyWhenInputValid() {
        Money money = Money.of(new BigDecimal("10.00"), "usd");

        assertEquals(new BigDecimal("10.00"), money.getAmount());
        assertEquals("USD", money.getCurrency());
    }

    @Test
    void shouldRejectNullAmount() {
        assertThrows(BusinessException.class, () -> Money.of(null, "USD"));
    }

    @Test
    void shouldRejectBlankCurrency() {
        assertThrows(BusinessException.class, () -> Money.of(new BigDecimal("10.00"), " "));
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThrows(BusinessException.class, () -> Money.of(new BigDecimal("-0.01"), "USD"));
    }

    @Test
    void shouldAddMoneyWithSameCurrency() {
        Money left = Money.of(new BigDecimal("3.50"), "USD");
        Money right = Money.of(new BigDecimal("6.25"), "USD");

        Money total = left.add(right);

        assertEquals(new BigDecimal("9.75"), total.getAmount());
        assertEquals("USD", total.getCurrency());
    }

    @Test
    void shouldRejectAddingDifferentCurrencies() {
        Money usd = Money.of(new BigDecimal("1.00"), "USD");
        Money eur = Money.of(new BigDecimal("1.00"), "EUR");

        assertThrows(BusinessException.class, () -> usd.add(eur));
    }

    @Test
    void shouldCompareByValueAndCurrency() {
        Money first = Money.of(new BigDecimal("2.0"), "USD");
        Money second = Money.of(new BigDecimal("2.00"), "USD");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
