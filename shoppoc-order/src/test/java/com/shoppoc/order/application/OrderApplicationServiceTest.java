package com.shoppoc.order.application;

import com.shoppoc.catalog.api.ProductDto;
import com.shoppoc.catalog.api.ProductLookupPort;
import com.shoppoc.order.api.OrderDto;
import com.shoppoc.order.domain.Order;
import com.shoppoc.order.domain.OrderRepository;
import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.NotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderApplicationServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final ProductLookupPort productLookupPort = mock(ProductLookupPort.class);
    private final OrderApplicationService service = new OrderApplicationService(orderRepository, productLookupPort);

    @Test
    void createOrderSuccessOneProduct() {
        when(productLookupPort.findProductById(eq("p1"))).thenReturn(Optional.of(product("p1", "SKU-1", "Phone", "10.00", "EUR", 10)));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto result = service.createOrder(new CreateOrderCommand("user@example.com",
                Collections.singletonList(new CreateOrderLineCommand("p1", 2))));

        assertEquals("CREATED", result.getStatus());
        assertEquals(new BigDecimal("20.00"), result.getTotalAmount());
        assertEquals(1, result.getLines().size());
    }

    @Test
    void createOrderSuccessTwoProductsCorrectTotal() {
        when(productLookupPort.findProductById(eq("p1"))).thenReturn(Optional.of(product("p1", "SKU-1", "Phone", "10.00", "EUR", 10)));
        when(productLookupPort.findProductById(eq("p2"))).thenReturn(Optional.of(product("p2", "SKU-2", "Case", "5.00", "EUR", 10)));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto result = service.createOrder(new CreateOrderCommand("user@example.com", Arrays.asList(
                new CreateOrderLineCommand("p1", 2),
                new CreateOrderLineCommand("p2", 1)
        )));

        assertEquals(new BigDecimal("25.00"), result.getTotalAmount());
        assertEquals(2, result.getLines().size());
    }

    @Test
    void invalidProductThrowsNotFound() {
        when(productLookupPort.findProductById(eq("missing"))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.createOrder(new CreateOrderCommand(
                "user@example.com",
                Collections.singletonList(new CreateOrderLineCommand("missing", 1))
        )));
    }

    @Test
    void emptyLinesRejected() {
        assertThrows(BusinessException.class, () -> service.createOrder(new CreateOrderCommand("user@example.com", Collections.emptyList())));
    }

    @Test
    void quantityGreaterThanStockThrowsBusinessException() {
        when(productLookupPort.findProductById(eq("p1"))).thenReturn(Optional.of(product("p1", "SKU-1", "Phone", "10.00", "EUR", 1)));

        assertThrows(BusinessException.class, () -> service.createOrder(new CreateOrderCommand(
                "user@example.com",
                Collections.singletonList(new CreateOrderLineCommand("p1", 2))
        )));
    }

    private ProductDto product(String id, String sku, String name, String amount, String currency, int stock) {
        return new ProductDto(id, sku, name, "desc", new BigDecimal(amount), currency, stock, "ACTIVE");
    }
}
