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
    private final String paymentId;
    private final String paymentReference;
    private final String paymentStatus;
    private final String paymentRejectionReason;
    private final Instant createdAt;

    private Order(OrderId id,
                  String customerEmail,
                  List<OrderLine> lines,
                  Money total,
                  OrderStatus status,
                  String paymentId,
                  String paymentReference,
                  String paymentStatus,
                  String paymentRejectionReason,
                  Instant createdAt) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.lines = lines;
        this.total = total;
        this.status = status;
        this.paymentId = paymentId;
        this.paymentReference = paymentReference;
        this.paymentStatus = paymentStatus;
        this.paymentRejectionReason = paymentRejectionReason;
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
                null,
                null,
                null,
                null,
                Instant.now()
        );
    }

    public static Order rehydrate(OrderId id,
                                  String customerEmail,
                                  List<OrderLine> lines,
                                  OrderStatus status,
                                  String paymentId,
                                  String paymentReference,
                                  String paymentStatus,
                                  String paymentRejectionReason,
                                  Instant createdAt) {
        if (status == null) {
            throw new BusinessException(DomainError.validation("Order status must not be null"));
        }
        if (createdAt == null) {
            throw new BusinessException(DomainError.validation("CreatedAt must not be null"));
        }
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
                status,
                paymentId,
                paymentReference,
                paymentStatus,
                paymentRejectionReason,
                createdAt
        );
    }

    public Order markPaid(String paymentId, String paymentReference) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Payment id must not be blank"));
        }
        if (paymentReference == null || paymentReference.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Payment reference must not be blank"));
        }

        return new Order(
                id,
                customerEmail,
                lines,
                total,
                OrderStatus.PAID,
                paymentId.trim(),
                paymentReference.trim(),
                "AUTHORIZED",
                null,
                createdAt
        );
    }

    public Order markPaymentRejected(String paymentId, String paymentReference, String reason) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Payment id must not be blank"));
        }
        if (paymentReference == null || paymentReference.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Payment reference must not be blank"));
        }

        return new Order(
                id,
                customerEmail,
                lines,
                total,
                OrderStatus.PAYMENT_REJECTED,
                paymentId.trim(),
                paymentReference.trim(),
                "REJECTED",
                reason,
                createdAt
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

    public String getPaymentId() {
        return paymentId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentRejectionReason() {
        return paymentRejectionReason;
    }

    public boolean belongsTo(String customerEmail) {
        return this.customerEmail.equals(customerEmail);
    }
}
