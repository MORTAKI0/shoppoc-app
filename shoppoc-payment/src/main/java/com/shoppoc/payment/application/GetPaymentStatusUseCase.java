package com.shoppoc.payment.application;

import com.shoppoc.payment.api.PaymentDto;

public interface GetPaymentStatusUseCase {

    PaymentDto getPayment(String paymentId);
}
