package com.shoppoc.payment.application;

import java.math.BigDecimal;

public class AuthorizePaymentCommand {

    private final BigDecimal amount;
    private final String currency;
    private final String orderReference;
    private final String paymentMethodToken;

    public AuthorizePaymentCommand(BigDecimal amount,
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
