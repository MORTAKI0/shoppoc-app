package com.shoppoc.payment.api;

public interface PaymentAuthorizationPort {

    PaymentDto authorize(PaymentAuthorizationRequest request);
}
