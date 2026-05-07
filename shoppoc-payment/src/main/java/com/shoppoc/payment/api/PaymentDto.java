package com.shoppoc.payment.api;

import java.math.BigDecimal;

public class PaymentDto {

    private final String id;
    private final String reference;
    private final BigDecimal amount;
    private final String currency;
    private final String status;
    private final String provider;
    private final String rejectionReason;

    public PaymentDto(String id,
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
