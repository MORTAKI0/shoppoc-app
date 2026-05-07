package com.shoppoc.payment.infrastructure.web;

import com.shoppoc.payment.api.PaymentDto;

import java.math.BigDecimal;

public class PaymentResponse {

    private final String id;
    private final String reference;
    private final BigDecimal amount;
    private final String currency;
    private final String status;
    private final String provider;
    private final String rejectionReason;

    public PaymentResponse(String id,
                           String reference,
                           BigDecimal amount,
                           String currency,
                           String status,
                           String provider,
                           String rejectionReason) {
        this.id = id;
        this.reference = reference;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.provider = provider;
        this.rejectionReason = rejectionReason;
    }

    public static PaymentResponse fromDto(PaymentDto paymentDto) {
        return new PaymentResponse(
                paymentDto.getId(),
                paymentDto.getReference(),
                paymentDto.getAmount(),
                paymentDto.getCurrency(),
                paymentDto.getStatus(),
                paymentDto.getProvider(),
                paymentDto.getRejectionReason()
        );
    }

    public String getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public String getProvider() {
        return provider;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}
