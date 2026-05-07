package com.shoppoc.payment.infrastructure.persistence;

import com.shoppoc.payment.domain.Payment;
import com.shoppoc.payment.domain.PaymentId;
import com.shoppoc.payment.domain.PaymentReference;
import com.shoppoc.payment.domain.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaPaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentRepository springDataPaymentRepository;

    public JpaPaymentRepositoryAdapter(SpringDataPaymentRepository springDataPaymentRepository) {
        this.springDataPaymentRepository = springDataPaymentRepository;
    }

    @Override
    public Payment save(Payment payment) {
        JpaPaymentEntity saved = springDataPaymentRepository.save(toEntity(payment));
        return toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        return springDataPaymentRepository.findById(id.value()).map(this::toDomain);
    }

    private Payment toDomain(JpaPaymentEntity entity) {
        if (entity.getStatus().name().equals("AUTHORIZED")) {
            return Payment.authorized(
                    PaymentId.fromString(entity.getId()),
                    PaymentReference.of(entity.getReference()),
                    entity.getAmount(),
                    entity.getCurrency(),
                    entity.getProvider()
            );
        }
        return Payment.rejected(
                PaymentId.fromString(entity.getId()),
                PaymentReference.of(entity.getReference()),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getProvider(),
                entity.getRejectionReason()
        );
    }

    private JpaPaymentEntity toEntity(Payment payment) {
        return new JpaPaymentEntity(
                payment.getId().value(),
                payment.getReference().value(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getProvider(),
                payment.getRejectionReason(),
                payment.getProcessedAt()
        );
    }
}
