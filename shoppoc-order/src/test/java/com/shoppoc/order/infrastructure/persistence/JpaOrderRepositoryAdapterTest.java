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

    private Order sampleOrder() {
        OrderLine line = OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(2), Money.of(new BigDecimal("10.00"), "EUR"));
        return Order.create(OrderId.newId(), "user@example.com", Collections.singletonList(line));
    }
}
