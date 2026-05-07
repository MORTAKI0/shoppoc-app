package com.shoppoc.payment.application;

import com.shoppoc.payment.api.PaymentDto;
import com.shoppoc.payment.domain.Payment;
import com.shoppoc.payment.domain.PaymentId;
import com.shoppoc.payment.domain.PaymentReference;
import com.shoppoc.payment.domain.PaymentRepository;
import com.shoppoc.shared.error.NotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentApplicationServiceTest {

    @Test
    void authorizeNormalAmountReturnsAuthorized() {
        InMemoryPaymentRepository repository = new InMemoryPaymentRepository();
        PaymentApplicationService service = new PaymentApplicationService(repository, new LocalPaymentProvider());

        PaymentDto payment = service.authorize(new AuthorizePaymentCommand(new BigDecimal("99.99"), "EUR", "ORDER-1", "stub-ok"));

        assertEquals("AUTHORIZED", payment.getStatus());
        assertEquals("LOCAL_STUB", payment.getProvider());
        assertTrue(repository.savedCount > 0);
    }

    @Test
    void authorizeRejectTokenReturnsRejected() {
        PaymentApplicationService service = new PaymentApplicationService(new InMemoryPaymentRepository(), new LocalPaymentProvider());

        PaymentDto payment = service.authorize(new AuthorizePaymentCommand(new BigDecimal("99.99"), "EUR", "ORDER-1", "reject"));

        assertEquals("REJECTED", payment.getStatus());
    }

    @Test
    void authorizeOverLimitReturnsRejected() {
        PaymentApplicationService service = new PaymentApplicationService(new InMemoryPaymentRepository(), new LocalPaymentProvider());

        PaymentDto payment = service.authorize(new AuthorizePaymentCommand(new BigDecimal("10000.01"), "EUR", "ORDER-1", "stub-ok"));

        assertEquals("REJECTED", payment.getStatus());
        assertEquals("Amount exceeds local stub limit", payment.getRejectionReason());
    }

    @Test
    void getPaymentMissingThrowsNotFound() {
        PaymentApplicationService service = new PaymentApplicationService(new InMemoryPaymentRepository(), new LocalPaymentProvider());

        assertThrows(NotFoundException.class, () -> service.getPayment(PaymentId.newId().value()));
    }

    private static class InMemoryPaymentRepository implements PaymentRepository {

        private final Map<String, Payment> store = new HashMap<String, Payment>();
        private int savedCount;

        @Override
        public Payment save(Payment payment) {
            savedCount++;
            store.put(payment.getId().value(), payment);
            return payment;
        }

        @Override
        public Optional<Payment> findById(PaymentId id) {
            return Optional.ofNullable(store.get(id.value()));
        }
    }
}
