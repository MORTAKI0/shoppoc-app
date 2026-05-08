package com.shoppoc.order.infrastructure.web;

import com.shoppoc.order.api.AdminOrderSummaryDto;

import java.math.BigDecimal;

public class AdminOrderSummaryResponse {

    private final String id;
    private final String customerEmail;
    private final String status;
    private final BigDecimal totalAmount;
    private final String totalCurrency;
    private final String createdAt;
    private final String paymentStatus;
    private final String paymentId;
    private final String paymentReference;

    public AdminOrderSummaryResponse(String id,
                                     String customerEmail,
                                     String status,
                                     BigDecimal totalAmount,
                                     String totalCurrency,
                                     String createdAt,
                                     String paymentStatus,
                                     String paymentId,
                                     String paymentReference) {
        this.id = id;
        this.customerEmail = customerEmail;
        this.status = status;
        this.totalAmount = totalAmount;
        this.totalCurrency = totalCurrency;
        this.createdAt = createdAt;
        this.paymentStatus = paymentStatus;
        this.paymentId = paymentId;
        this.paymentReference = paymentReference;
    }

    public static AdminOrderSummaryResponse fromDto(AdminOrderSummaryDto dto) {
        return new AdminOrderSummaryResponse(
                dto.getId(),
                dto.getCustomerEmail(),
                dto.getStatus(),
                dto.getTotalAmount(),
                dto.getTotalCurrency(),
                dto.getCreatedAt(),
                dto.getPaymentStatus(),
                dto.getPaymentId(),
                dto.getPaymentReference()
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getTotalCurrency() {
        return totalCurrency;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }
}
