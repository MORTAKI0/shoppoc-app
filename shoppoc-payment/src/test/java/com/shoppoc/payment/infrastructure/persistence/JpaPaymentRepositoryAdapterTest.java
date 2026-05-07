package com.shoppoc.payment.infrastructure.persistence;

import com.shoppoc.payment.PaymentTestApplication;
import com.shoppoc.payment.domain.Payment;
import com.shoppoc.payment.domain.PaymentId;
import com.shoppoc.payment.domain.PaymentProvider;
import com.shoppoc.payment.domain.PaymentReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = PaymentTestApplication.class)
@Import(JpaPaymentRepositoryAdapter.class)
class JpaPaymentRepositoryAdapterTest {

    @Autowired
    private JpaPaymentRepositoryAdapter repositoryAdapter;

    @Test
    void saveAuthorizedPayment() {
        Payment saved = repositoryAdapter.save(Payment.authorized(
                PaymentId.newId(),
                PaymentReference.of("LOCAL-100"),
                new BigDecimal("49.99"),
                "EUR",
                PaymentProvider.LOCAL_STUB
        ));

        assertEquals("AUTHORIZED", saved.getStatus().name());
    }

    @Test
    void findByIdReturnsPayment() {
        Payment saved = repositoryAdapter.save(Payment.authorized(
                PaymentId.newId(),
                PaymentReference.of("LOCAL-101"),
                new BigDecimal("19.99"),
                "EUR",
                PaymentProvider.LOCAL_STUB
        ));

        Optional<Payment> found = repositoryAdapter.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId().value(), found.get().getId().value());
    }

    @Test
    void missingIdReturnsEmpty() {
        Optional<Payment> found = repositoryAdapter.findById(PaymentId.newId());

        assertFalse(found.isPresent());
    }
}
