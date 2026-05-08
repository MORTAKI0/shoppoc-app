package com.shoppoc.order.application;

import com.shoppoc.catalog.api.ProductDto;
import com.shoppoc.catalog.api.ProductLookupPort;
import com.shoppoc.order.api.OrderDto;
import com.shoppoc.order.domain.OrderId;
import com.shoppoc.order.domain.OrderLine;
import com.shoppoc.order.domain.Quantity;
import com.shoppoc.order.domain.Order;
import com.shoppoc.order.domain.OrderRepository;
import com.shoppoc.payment.api.PaymentAuthorizationPort;
import com.shoppoc.payment.api.PaymentAuthorizationRequest;
import com.shoppoc.payment.api.PaymentDto;
import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.shoppoc.shared.money.Money;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderApplicationServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final ProductLookupPort productLookupPort = mock(ProductLookupPort.class);
    private final PaymentAuthorizationPort paymentAuthorizationPort = mock(PaymentAuthorizationPort.class);
    private final OrderApplicationService service = new OrderApplicationService(
            orderRepository,
            productLookupPort,
            paymentAuthorizationPort
    );

    @Test
    void createOrderAuthorizedPaymentMarksPaid() {
        when(productLookupPort.findProductById(eq("p1"))).thenReturn(Optional.of(product("p1", "SKU-1", "Phone", "10.00", "EUR", 10)));
        when(paymentAuthorizationPort.authorize(any(PaymentAuthorizationRequest.class)))
                .thenReturn(new PaymentDto("pay-1", "ref-1", new BigDecimal("20.00"), "EUR", "AUTHORIZED", "LOCAL_STUB", null));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto result = service.createOrder(new CreateOrderCommand(
                "user@example.com",
                Collections.singletonList(new CreateOrderLineCommand("p1", 2)),
                "stub-ok"
        ));

        assertEquals("PAID", result.getStatus());
        assertEquals("pay-1", result.getPaymentId());
        assertEquals("ref-1", result.getPaymentReference());
        assertEquals("AUTHORIZED", result.getPaymentStatus());
        assertEquals(new BigDecimal("20.00"), result.getTotalAmount());
    }

    @Test
    void createOrderRejectedPaymentMarksRejected() {
        when(productLookupPort.findProductById(eq("p1"))).thenReturn(Optional.of(product("p1", "SKU-1", "Phone", "10.00", "EUR", 10)));
        when(paymentAuthorizationPort.authorize(any(PaymentAuthorizationRequest.class)))
                .thenReturn(new PaymentDto("pay-2", "ref-2", new BigDecimal("20.00"), "EUR", "REJECTED", "LOCAL_STUB", "Payment rejected by local stub token"));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto result = service.createOrder(new CreateOrderCommand(
                "user@example.com",
                Collections.singletonList(new CreateOrderLineCommand("p1", 2)),
                "reject"
        ));

        assertEquals("PAYMENT_REJECTED", result.getStatus());
        assertEquals("pay-2", result.getPaymentId());
        assertEquals("ref-2", result.getPaymentReference());
        assertEquals("REJECTED", result.getPaymentStatus());
        assertTrue(result.getPaymentRejectionReason().contains("rejected"));
    }

    @Test
    void paymentCalledWithCalculatedAmountCurrencyAndOrderReference() {
        when(productLookupPort.findProductById(eq("p1"))).thenReturn(Optional.of(product("p1", "SKU-1", "Phone", "10.00", "EUR", 10)));
        when(paymentAuthorizationPort.authorize(any(PaymentAuthorizationRequest.class)))
                .thenReturn(new PaymentDto("pay-1", "ref-1", new BigDecimal("20.00"), "EUR", "AUTHORIZED", "LOCAL_STUB", null));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createOrder(new CreateOrderCommand(
                "user@example.com",
                Collections.singletonList(new CreateOrderLineCommand("p1", 2)),
                "stub-ok"
        ));

        ArgumentCaptor<PaymentAuthorizationRequest> captor = ArgumentCaptor.forClass(PaymentAuthorizationRequest.class);
        verify(paymentAuthorizationPort).authorize(captor.capture());
        PaymentAuthorizationRequest request = captor.getValue();
        assertEquals(new BigDecimal("20.00"), request.getAmount());
        assertEquals("EUR", request.getCurrency());
        assertTrue(request.getOrderReference() != null && !request.getOrderReference().isEmpty());
        assertEquals("stub-ok", request.getPaymentMethodToken());
    }

    @Test
    void invalidProductThrowsNotFoundAndSkipsPayment() {
        when(productLookupPort.findProductById(eq("missing"))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.createOrder(new CreateOrderCommand(
                "user@example.com",
                Collections.singletonList(new CreateOrderLineCommand("missing", 1)),
                "stub-ok"
        )));

        verify(paymentAuthorizationPort, never()).authorize(any(PaymentAuthorizationRequest.class));
    }

    @Test
    void emptyLinesRejected() {
        assertThrows(BusinessException.class, () -> service.createOrder(new CreateOrderCommand(
                "user@example.com",
                Collections.<CreateOrderLineCommand>emptyList(),
                "stub-ok"
        )));
    }

    @Test
    void getOwnOrderReturnsDto() {
        Order order = sampleOrder("order-1", "user@example.com", "PAID");
        when(orderRepository.findById(eq(OrderId.fromString("order-1")))).thenReturn(Optional.of(order));

        OrderDto result = service.getOrder("order-1", "user@example.com");

        assertEquals("order-1", result.getId());
        assertEquals("PAID", result.getStatus());
        assertEquals(1, result.getLines().size());
        assertEquals(new BigDecimal("10.00"), result.getTotalAmount());
        assertEquals("EUR", result.getTotalCurrency());
        assertEquals("AUTHORIZED", result.getPaymentStatus());
    }

    @Test
    void getMissingOrderThrowsNotFound() {
        when(orderRepository.findById(eq(OrderId.fromString("missing")))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getOrder("missing", "user@example.com"));
    }

    @Test
    void getAnotherUsersOrderThrowsAccessDenied() {
        Order order = sampleOrder("order-2", "owner@example.com", "PAID");
        when(orderRepository.findById(eq(OrderId.fromString("order-2")))).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class, () -> service.getOrder("order-2", "other@example.com"));
    }

    @Test
    void listCurrentUserOrdersReturnsOnlyThatUserOrders() {
        Order first = sampleOrder("order-3", "user@example.com", "PAID");
        Order second = sampleOrder("order-4", "user@example.com", "PAYMENT_REJECTED");
        when(orderRepository.findByCustomerEmail(eq("user@example.com"))).thenReturn(Arrays.asList(first, second));

        List<OrderDto> result = service.listCurrentUserOrders("user@example.com");

        assertEquals(2, result.size());
        assertEquals("order-3", result.get(0).getId());
        assertEquals("order-4", result.get(1).getId());
        assertEquals(1, result.get(0).getLines().size());
        assertEquals(new BigDecimal("10.00"), result.get(0).getTotalAmount());
    }

    @Test
    void listCurrentUserOrdersReturnsEmptyWhenNone() {
        when(orderRepository.findByCustomerEmail(eq("user@example.com"))).thenReturn(Collections.<Order>emptyList());

        List<OrderDto> result = service.listCurrentUserOrders("user@example.com");

        assertTrue(result.isEmpty());
    }

    private ProductDto product(String id, String sku, String name, String amount, String currency, int stock) {
        return new ProductDto(id, sku, name, "desc", new BigDecimal(amount), currency, stock, "ACTIVE");
    }

    private Order sampleOrder(String orderId, String email, String status) {
        OrderLine line = OrderLine.of("p1", "SKU-1", "Phone", Quantity.of(1), Money.of(new BigDecimal("10.00"), "EUR"));
        Order created = Order.rehydrate(
                OrderId.fromString(orderId),
                email,
                Collections.singletonList(line),
                com.shoppoc.order.domain.OrderStatus.CREATED,
                null,
                null,
                null,
                null,
                java.time.Instant.now()
        );
        if ("PAID".equals(status)) {
            return created.markPaid("pay-1", "ref-1");
        }
        if ("PAYMENT_REJECTED".equals(status)) {
            return created.markPaymentRejected("pay-2", "ref-2", "Rejected by local stub");
        }
        return created;
    }
}
