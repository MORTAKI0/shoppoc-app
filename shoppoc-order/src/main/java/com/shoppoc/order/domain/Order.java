package com.shoppoc.order.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;
import com.shoppoc.shared.money.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Order {

    private final OrderId id;
    private final String customerEmail;
    private final List<OrderLine> lines;
    private final Money total;
    private final OrderStatus status;
    private final Instant createdAt;

    private Order(OrderId id,
                  String customerEmail,
                  List<OrderLine> lines,
                  Money total,
                  OrderStatus status,
                  Instant createdAt) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.lines = lines;
        this.total = total;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Order create(OrderId id, String customerEmail, List<OrderLine> lines) {
        if (id == null) {
            throw new BusinessException(DomainError.validation("Order id must not be null"));
        }
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Customer email must not be blank"));
        }
        if (lines == null || lines.isEmpty()) {
            throw new BusinessException(DomainError.validation("Order lines must not be empty"));
        }

        List<OrderLine> copy = new ArrayList<OrderLine>(lines);
        String currency = copy.get(0).getLineTotal().getCurrency();
        Money total = Money.zero(currency);

        for (OrderLine line : copy) {
            if (!currency.equals(line.getLineTotal().getCurrency())) {
                throw new BusinessException(DomainError.conflict("All order lines must use same currency"));
            }
            total = total.add(line.getLineTotal());
        }

        return new Order(
                id,
                customerEmail.trim(),
                Collections.unmodifiableList(copy),
                total,
                OrderStatus.CREATED,
                Instant.now()
        );
    }

    public OrderId getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public Money getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
