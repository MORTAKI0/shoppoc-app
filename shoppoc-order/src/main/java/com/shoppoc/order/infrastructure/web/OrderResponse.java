package com.shoppoc.order.infrastructure.web;

import com.shoppoc.order.api.OrderDto;
import com.shoppoc.order.api.OrderLineDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderResponse {

    private final String id;
    private final String customerEmail;
    private final String status;
    private final String paymentId;
    private final String paymentReference;
    private final String paymentStatus;
    private final String paymentRejectionReason;
    private final BigDecimal totalAmount;
    private final String totalCurrency;
    private final List<OrderLineResponse> lines;

    public OrderResponse(String id,
                         String customerEmail,
                         String status,
                         String paymentId,
                         String paymentReference,
                         String paymentStatus,
                         String paymentRejectionReason,
                         BigDecimal totalAmount,
                         String totalCurrency,
                         List<OrderLineResponse> lines) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.status = status;
        this.paymentId = paymentId;
        this.paymentReference = paymentReference;
        this.paymentStatus = paymentStatus;
        this.paymentRejectionReason = paymentRejectionReason;
        this.totalAmount = totalAmount;
        this.totalCurrency = totalCurrency;
        this.lines = lines;
    }

    public static OrderResponse fromDto(OrderDto dto) {
        List<OrderLineResponse> lineResponses = new ArrayList<OrderLineResponse>();
        for (OrderLineDto line : dto.getLines()) {
            lineResponses.add(OrderLineResponse.fromDto(line));
        }
        return new OrderResponse(
                dto.getId(),
                dto.getCustomerEmail(),
                dto.getStatus(),
                dto.getPaymentId(),
                dto.getPaymentReference(),
                dto.getPaymentStatus(),
                dto.getPaymentRejectionReason(),
                dto.getTotalAmount(),
                dto.getTotalCurrency(),
                lineResponses
        );
    }

    public String getId() {
        return id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getStatus() {
        return status;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getTotalCurrency() {
        return totalCurrency;
    }

    public List<OrderLineResponse> getLines() {
        return lines;
    }
}
