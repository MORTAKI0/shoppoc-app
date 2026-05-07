package com.shoppoc.payment.application;

import com.shoppoc.payment.api.PaymentDto;

public interface AuthorizePaymentUseCase {

    PaymentDto authorize(AuthorizePaymentCommand command);
}
