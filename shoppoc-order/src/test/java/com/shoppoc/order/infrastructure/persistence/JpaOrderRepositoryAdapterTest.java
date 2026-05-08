package com.shoppoc.order.infrastructure.persistence;

import com.shoppoc.order.OrderTestApplication;
import com.shoppoc.order.domain.Order;
import com.shoppoc.order.domain.OrderId;
import com.shoppoc.order.domain.OrderLine;
import com.shoppoc.order.domain.Quantity;
import com.shoppoc.shared.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = OrderTestApplication.class)
@Import(JpaOrderRepositoryAdapter.class)
class JpaOrderRepositoryAdapterTest {

    @Autowired
    private JpaOrderRepositoryAdapter repository;

    @Test
    void saveOrderWithOneLine() {
        Order order = sampleOrder();

        Order saved = repository.save(order);

        assertEquals(order.getId().value(), saved.getId().value());
        assertEquals(1, saved.getLines().size());
    }

    @Test
    void findByIdReturnsOrder() {
        Order order = repository.save(sampleOrder());

        Optional<Order> found = repository.findById(order.getId());

        assertTrue(found.isPresent());
    }

    @Test
    void lineTotalPersisted() {
        Order order = repository.save(sampleOrder());

        Optional<Order> found = repository.findById(order.getId());

        assertEquals(new BigDecimal("20.00"), found.get().getLines().get(0).getLineTotal().getAmount());
    }

    @Test
    void orderTotalPersisted() {
        Order order = repository.save(sampleOrder());

        Optional<Order> found = repository.findById(order.getId());

        assertEquals(new BigDecimal("20.00"), found.get().getTotal().getAmount());
    }

    @Test
    void missingIdReturnsEmpty() {
        Optional<Order> found = repository.findById(OrderId.newId());

        assertFalse(found.isPresent());
    }

    @Test
    void saveAndFindPaidOrderPreservesPaymentData() {
        Order paidOrder = sampleOrder().markPaid("pay-1", "ref-1");

        Order saved = repository.save(paidOrder);
        Optional<Order> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("PAID", found.get().getStatus().name());
        assertEquals("pay-1", found.get().getPaymentId());
        assertEquals("ref-1", found.get().getPaymentReference());
        assertEquals("AUTHORIZED", found.get().getPaymentStatus());
    }

    @Test
    void saveAndFindRejectedOrderPreservesRejectionReason() {
        Order rejectedOrder = sampleOrder().markPaymentRejected("pay-2", "ref-2", "Rejected by local stub");

        Order saved = repository.save(rejectedOrder);
        Optional<Order> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("PAYMENT_REJECTED", found.get().getStatus().name());
        assertEquals("REJECTED", found.get().getPaymentStatus());
        assertEquals("Rejected by local stub", found.get().getPaymentRejectionReason());
    }

    @Test
    void findByCustomerEmailReturnsOnlyMatchingCustomerOrders() {
        repository.save(sampleOrder("a1", "user-a@example.com"));
        repository.save(sampleOrder("a2", "user-a@example.com"));
        repository.save(sampleOrder("b1", "user-b@example.com"));

        List<Order> userAOrders = repository.findByCustomerEmail("user-a@example.com");
        List<Order> userBOrders = repository.findByCustomerEmail("user-b@example.com");

        assertEquals(2, userAOrders.size());
        assertTrue(userAOrders.get(0).belongsTo("user-a@example.com"));
        assertTrue(userAOrders.get(1).belongsTo("user-a@example.com"));
        assertEquals(1, userBOrders.size());
        assertTrue(userBOrders.get(0).belongsTo("user-b@example.com"));
    }

    @Test
    void findByIdPreservesLinesTotalStatusAndPaymentFields() {
        Order paidOrder = sampleOrder("paid-1", "user@example.com").markPaid("pay-9", "ref-9");
        repository.save(paidOrder);

        Optional<Order> found = repository.findById(OrderId.fromString("paid-1"));

        assertTrue(found.isPresent());
        assertEquals(1, found.get().getLines().size());
        assertEquals(new BigDecimal("20.00"), found.get().getTotal().getAmount());
        assertEquals("PAID", found.get().getStatus().name());
        assertEquals("pay-9", found.get().getPaymentId());
        assertEquals("AUTHORIZED", found.get().getPaymentStatus());
    }

    @Test
    void findAllReturnsOrdersAcrossUsersWithFieldsPreserved() {
        repository.save(sampleOrder("a1", "user-a@example.com", Instant.parse("2026-01-01T10:00:00Z")));
        repository.save(sampleOrder("b1", "user-b@example.com", Instant.parse("2026-01-02T10:00:00Z")).markPaid("pay-1", "ref-1"));

        List<Order> allOrders = repository.findAll();
        List<Order> userAOrders = repository.findByCustomerEmail("user-a@example.com");

        assertEquals(2, allOrders.size());
        assertEquals("b1", allOrders.get(0).getId().value());
        assertEquals("user-b@example.com", allOrders.get(0).getCustomerEmail());
        assertEquals(new BigDecimal("20.00"), allOrders.get(0).getTotal().getAmount());
        assertEquals("PAID", allOrders.get(0).getStatus().name());
        assertEquals(Instant.parse("2026-01-02T10:00:00Z"), allOrders.get(0).getCreatedAt());
        assertEquals("AUTHORIZED", allOrders.get(0).getPaymentStatus());
        assertEquals(1, userAOrders.size());
        assertEquals("a1", userAOrders.get(0).getId().value());
    }

    private Order sampleOrder() {
        OrderLine line = OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(2), Money.of(new BigDecimal("10.00"), "EUR"));
        return Order.create(OrderId.newId(), "user@example.com", Collections.singletonList(line));
    }

    private Order sampleOrder(String id, String customerEmail) {
        return sampleOrder(id, customerEmail, Instant.now());
    }

    private Order sampleOrder(String id, String customerEmail, Instant createdAt) {
        OrderLine line = OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(2), Money.of(new BigDecimal("10.00"), "EUR"));
        return Order.rehydrate(
                OrderId.fromString(id),
                customerEmail,
                Collections.singletonList(line),
                com.shoppoc.order.domain.OrderStatus.CREATED,
                null,
                null,
                null,
                null,
                createdAt
        );
    }
}
