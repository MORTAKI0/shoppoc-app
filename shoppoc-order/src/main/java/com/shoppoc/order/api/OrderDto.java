package com.shoppoc.order.api;

import java.math.BigDecimal;
import java.util.List;

public class OrderDto {

    private final String id;
    private final String customerEmail;
    private final String status;
    private final String paymentId;
    private final String paymentReference;
    private final String paymentStatus;
    private final String paymentRejectionReason;
    private final BigDecimal totalAmount;
    private final String totalCurrency;
    private final List<OrderLineDto> lines;

    public OrderDto(String id,
                    String customerEmail,
                    String status,
                    String paymentId,
                    String paymentReference,
                    String paymentStatus,
                    String paymentRejectionReason,
                    BigDecimal totalAmount,
                    String totalCurrency,
                    List<OrderLineDto> lines) {
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

    public List<OrderLineDto> getLines() {
        return lines;
    }
}
