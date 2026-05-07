package com.shoppoc.payment.application;

import com.shoppoc.payment.domain.Payment;
import com.shoppoc.payment.domain.PaymentId;
import com.shoppoc.payment.domain.PaymentProvider;
import com.shoppoc.payment.domain.PaymentReference;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

public class LocalPaymentProvider {

    private static final BigDecimal LIMIT = new BigDecimal("10000");

    public PaymentDecision authorize(PaymentId paymentId,
                                     BigDecimal amount,
                                     String currency,
                                     String orderReference,
                                     String paymentMethodToken) {
        PaymentReference reference = PaymentReference.of("LOCAL-" + UUID.randomUUID());

        if (amount.compareTo(LIMIT) > 0) {
            return PaymentDecision.rejected(Payment.rejected(
                    paymentId,
                    reference,
                    amount,
                    currency,
                    PaymentProvider.LOCAL_STUB,
                    "Amount exceeds local stub limit"
            ));
        }

        if (paymentMethodToken != null && "reject".equalsIgnoreCase(paymentMethodToken.trim())) {
            return PaymentDecision.rejected(Payment.rejected(
                    paymentId,
                    reference,
                    amount,
                    currency,
                    PaymentProvider.LOCAL_STUB,
                    "Payment rejected by local stub token"
            ));
        }

        return PaymentDecision.authorized(Payment.authorized(
                paymentId,
                reference,
                amount,
                currency == null ? null : currency.toUpperCase(Locale.ROOT),
                PaymentProvider.LOCAL_STUB
        ));
    }

    public static class PaymentDecision {

        private final Payment payment;

        private PaymentDecision(Payment payment) {
            this.payment = payment;
        }

        public static PaymentDecision authorized(Payment payment) {
            return new PaymentDecision(payment);
        }

        public static PaymentDecision rejected(Payment payment) {
            return new PaymentDecision(payment);
        }

        public Payment getPayment() {
            return payment;
        }
    }
}
