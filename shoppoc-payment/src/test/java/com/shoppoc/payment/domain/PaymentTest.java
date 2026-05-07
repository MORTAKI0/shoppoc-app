package com.shoppoc.payment.domain;

import com.shoppoc.shared.error.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentTest {

    @Test
    void authorizedPaymentIsValid() {
        Payment payment = Payment.authorized(
                PaymentId.newId(),
                PaymentReference.of("LOCAL-1"),
                new BigDecimal("99.99"),
                "EUR",
                PaymentProvider.LOCAL_STUB
        );

        assertEquals(PaymentStatus.AUTHORIZED, payment.getStatus());
        assertEquals("EUR", payment.getCurrency());
    }

    @Test
    void rejectedPaymentIsValid() {
        Payment payment = Payment.rejected(
                PaymentId.newId(),
                PaymentReference.of("LOCAL-2"),
                new BigDecimal("99.99"),
                "EUR",
                PaymentProvider.LOCAL_STUB,
                "Declined"
        );

        assertEquals(PaymentStatus.REJECTED, payment.getStatus());
        assertEquals("Declined", payment.getRejectionReason());
    }

    @Test
    void zeroOrNegativeAmountIsRejected() {
        assertThrows(BusinessException.class, () -> Payment.authorized(
                PaymentId.newId(),
                PaymentReference.of("LOCAL-3"),
                BigDecimal.ZERO,
                "EUR",
                PaymentProvider.LOCAL_STUB
        ));

        assertThrows(BusinessException.class, () -> Payment.authorized(
                PaymentId.newId(),
                PaymentReference.of("LOCAL-4"),
                new BigDecimal("-10"),
                "EUR",
                PaymentProvider.LOCAL_STUB
        ));
    }

    @Test
    void blankCurrencyIsRejected() {
        assertThrows(BusinessException.class, () -> Payment.authorized(
                PaymentId.newId(),
                PaymentReference.of("LOCAL-5"),
                new BigDecimal("10.00"),
                " ",
                PaymentProvider.LOCAL_STUB
        ));
    }
}
