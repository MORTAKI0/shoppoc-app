package com.shoppoc.order.application;

import com.shoppoc.catalog.api.ProductLookupPort;
import com.shoppoc.order.api.AdminOrderSummaryDto;
import com.shoppoc.order.domain.Order;
import com.shoppoc.order.domain.OrderId;
import com.shoppoc.order.domain.OrderLine;
import com.shoppoc.order.domain.OrderRepository;
import com.shoppoc.order.domain.OrderStatus;
import com.shoppoc.order.domain.Quantity;
import com.shoppoc.payment.api.PaymentAuthorizationPort;
import com.shoppoc.shared.money.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class OrderApplicationServiceAdminTest {

    @Test
    void listAllOrdersReturnsMultipleUsersNewestFirst() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        repository.add(sampleOrder("order-1", "user-a@example.com", OrderStatus.CREATED, null, null, null, Instant.parse("2026-01-01T10:00:00Z")));
        repository.add(sampleOrder("order-2", "user-b@example.com", OrderStatus.PAID, "pay-2", "ref-2", "AUTHORIZED", Instant.parse("2026-01-02T10:00:00Z")));
        OrderApplicationService service = service(repository);

        List<AdminOrderSummaryDto> result = service.listAllOrders();

        assertEquals(2, result.size());
        assertEquals("order-2", result.get(0).getId());
        assertEquals("user-b@example.com", result.get(0).getCustomerEmail());
        assertEquals("PAID", result.get(0).getStatus());
        assertEquals(new BigDecimal("10.00"), result.get(0).getTotalAmount());
        assertEquals("EUR", result.get(0).getTotalCurrency());
        assertEquals("2026-01-02T10:00:00Z", result.get(0).getCreatedAt());
        assertEquals("AUTHORIZED", result.get(0).getPaymentStatus());
        assertEquals("pay-2", result.get(0).getPaymentId());
        assertEquals("ref-2", result.get(0).getPaymentReference());
    }

    @Test
    void listAllOrdersReturnsEmptyWhenRepositoryEmpty() {
        OrderApplicationService service = service(new InMemoryOrderRepository());

        List<AdminOrderSummaryDto> result = service.listAllOrders();

        assertTrue(result.isEmpty());
    }

    @Test
    void listAllOrdersIncludesNullablePaymentFields() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        repository.add(sampleOrder("order-3", "user-c@example.com", OrderStatus.CREATED, null, null, null, Instant.parse("2026-01-03T10:00:00Z")));
        OrderApplicationService service = service(repository);

        List<AdminOrderSummaryDto> result = service.listAllOrders();

        assertEquals(1, result.size());
        assertNull(result.get(0).getPaymentStatus());
        assertNull(result.get(0).getPaymentId());
        assertNull(result.get(0).getPaymentReference());
    }

    private OrderApplicationService service(OrderRepository repository) {
        ProductLookupPort productLookupPort = mock(ProductLookupPort.class);
        PaymentAuthorizationPort paymentAuthorizationPort = mock(PaymentAuthorizationPort.class);
        return new OrderApplicationService(repository, productLookupPort, paymentAuthorizationPort);
    }

    private Order sampleOrder(String id,
                              String customerEmail,
                              OrderStatus status,
                              String paymentId,
                              String paymentReference,
                              String paymentStatus,
                              Instant createdAt) {
        OrderLine line = OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(1), Money.of(new BigDecimal("10.00"), "EUR"));
        return Order.rehydrate(
                OrderId.fromString(id),
                customerEmail,
                Collections.singletonList(line),
                status,
                paymentId,
                paymentReference,
                paymentStatus,
                null,
                createdAt
        );
    }

    private static class InMemoryOrderRepository implements OrderRepository {

        private final List<Order> orders = new ArrayList<Order>();

        @Override
        public Order save(Order order) {
            orders.add(order);
            return order;
        }

        @Override
        public Optional<Order> findById(OrderId id) {
            for (Order order : orders) {
                if (order.getId().equals(id)) {
                    return Optional.of(order);
                }
            }
            return Optional.empty();
        }

        @Override
        public List<Order> findAll() {
            List<Order> copy = new ArrayList<Order>(orders);
            copy.sort((left, right) -> right.getCreatedAt().compareTo(left.getCreatedAt()));
            return copy;
        }

        @Override
        public List<Order> findByCustomerEmail(String customerEmail) {
            List<Order> byCustomer = new ArrayList<Order>();
            for (Order order : orders) {
                if (order.belongsTo(customerEmail)) {
                    byCustomer.add(order);
                }
            }
            return byCustomer;
        }

        void add(Order order) {
            orders.add(order);
        }
    }
}
