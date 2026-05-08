package com.shoppoc.order.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.money.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderTest {

    @Test
    void oneLineTotalCorrect() {
        OrderLine line = OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(2), Money.of(new BigDecimal("10.00"), "EUR"));
        Order order = Order.create(OrderId.newId(), "user@example.com", Collections.singletonList(line));

        assertEquals(new BigDecimal("20.00"), order.getTotal().getAmount());
        assertEquals("CREATED", order.getStatus().name());
    }

    @Test
    void multipleLinesTotalCorrect() {
        OrderLine line1 = OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(2), Money.of(new BigDecimal("10.00"), "EUR"));
        OrderLine line2 = OrderLine.of("p2", "SKU-2", "Case", Quantity.of(1), Money.of(new BigDecimal("5.00"), "EUR"));

        Order order = Order.create(OrderId.newId(), "user@example.com", Arrays.asList(line1, line2));

        assertEquals(new BigDecimal("25.00"), order.getTotal().getAmount());
    }

    @Test
    void rejectsEmptyLines() {
        assertThrows(BusinessException.class, () -> Order.create(OrderId.newId(), "user@example.com", Collections.emptyList()));
    }

    @Test
    void rejectsZeroOrNegativeQuantity() {
        assertThrows(BusinessException.class, () -> Quantity.of(0));
        assertThrows(BusinessException.class, () -> Quantity.of(-1));
    }

    @Test
    void rejectsMixedCurrencies() {
        OrderLine line1 = OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(1), Money.of(new BigDecimal("10.00"), "EUR"));
        OrderLine line2 = OrderLine.of("p2", "SKU-2", "Case", Quantity.of(1), Money.of(new BigDecimal("5.00"), "USD"));

        assertThrows(BusinessException.class, () -> Order.create(OrderId.newId(), "user@example.com", Arrays.asList(line1, line2)));
    }

    @Test
    void markPaidUpdatesStatusAndPaymentFields() {
        Order order = Order.create(OrderId.newId(), "user@example.com", Collections.singletonList(
                OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(1), Money.of(new BigDecimal("10.00"), "EUR"))
        ));

        Order paid = order.markPaid("pay-1", "ref-1");

        assertEquals("PAID", paid.getStatus().name());
        assertEquals("pay-1", paid.getPaymentId());
        assertEquals("ref-1", paid.getPaymentReference());
        assertEquals("AUTHORIZED", paid.getPaymentStatus());
    }

    @Test
    void markPaymentRejectedUpdatesStatusAndReason() {
        Order order = Order.create(OrderId.newId(), "user@example.com", Collections.singletonList(
                OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(1), Money.of(new BigDecimal("10.00"), "EUR"))
        ));

        Order rejected = order.markPaymentRejected("pay-2", "ref-2", "Rejected by provider");

        assertEquals("PAYMENT_REJECTED", rejected.getStatus().name());
        assertEquals("REJECTED", rejected.getPaymentStatus());
        assertTrue(rejected.getPaymentRejectionReason().contains("Rejected"));
    }

    @Test
    void markPaidRejectsBlankPaymentIdOrReference() {
        Order order = Order.create(OrderId.newId(), "user@example.com", Collections.singletonList(
                OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(1), Money.of(new BigDecimal("10.00"), "EUR"))
        ));

        assertThrows(BusinessException.class, () -> order.markPaid(" ", "ref-1"));
        assertThrows(BusinessException.class, () -> order.markPaid("pay-1", " "));
    }

    @Test
    void belongsToSameCustomerEmail() {
        Order order = Order.create(OrderId.newId(), "user@example.com", Collections.singletonList(
                OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(1), Money.of(new BigDecimal("10.00"), "EUR"))
        ));

        assertTrue(order.belongsTo("user@example.com"));
    }

    @Test
    void doesNotBelongToDifferentCustomerEmail() {
        Order order = Order.create(OrderId.newId(), "user@example.com", Collections.singletonList(
                OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(1), Money.of(new BigDecimal("10.00"), "EUR"))
        ));

        assertFalse(order.belongsTo("other@example.com"));
    }
}
