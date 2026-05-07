package com.shoppoc.payment.api;

import java.math.BigDecimal;

public class PaymentAuthorizationRequest {

    private final BigDecimal amount;
    private final String currency;
    private final String orderReference;
    private final String paymentMethodToken;

    public PaymentAuthorizationRequest(BigDecimal amount,
                                       String currency,
                                       String orderReference,
                                       String paymentMethodToken) {
        this.amount = amount;
        this.currency = currency;
        this.orderReference = orderReference;
        this.paymentMethodToken = paymentMethodToken;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public String getPaymentMethodToken() {
        return paymentMethodToken;
    }
}
