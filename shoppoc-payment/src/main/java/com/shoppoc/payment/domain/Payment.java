package com.shoppoc.payment.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {

    private final PaymentId id;
    private final PaymentReference reference;
    private final BigDecimal amount;
    private final String currency;
    private final PaymentStatus status;
    private final PaymentProvider provider;
    private final String rejectionReason;
    private final Instant processedAt;

    private Payment(PaymentId id,
                    PaymentReference reference,
                    BigDecimal amount,
                    String currency,
                    PaymentStatus status,
                    PaymentProvider provider,
                    String rejectionReason,
                    Instant processedAt) {
        if (id == null) {
            throw new BusinessException(DomainError.validation("Payment id must not be null"));
        }
        if (reference == null) {
            throw new BusinessException(DomainError.validation("Payment reference must not be null"));
        }
        if (amount == null) {
            throw new BusinessException(DomainError.validation("Payment amount must not be null"));
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(DomainError.validation("Payment amount must be greater than zero"));
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Payment currency must not be blank"));
        }
        if (status == null) {
            throw new BusinessException(DomainError.validation("Payment status must not be null"));
        }
        if (provider == null) {
            throw new BusinessException(DomainError.validation("Payment provider must not be null"));
        }
        if (PaymentStatus.REJECTED.equals(status) && (rejectionReason == null || rejectionReason.trim().isEmpty())) {
            throw new BusinessException(DomainError.validation("Rejection reason must not be blank for rejected payment"));
        }

        this.id = id;
        this.reference = reference;
        this.amount = amount;
        this.currency = currency.trim();
        this.status = status;
        this.provider = provider;
        this.rejectionReason = rejectionReason == null ? null : rejectionReason.trim();
        this.processedAt = processedAt == null ? Instant.now() : processedAt;
    }

    public static Payment authorized(PaymentId id,
                                     PaymentReference reference,
                                     BigDecimal amount,
                                     String currency,
                                     PaymentProvider provider) {
        return new Payment(id, reference, amount, currency, PaymentStatus.AUTHORIZED, provider, null, Instant.now());
    }

    public static Payment rejected(PaymentId id,
                                   PaymentReference reference,
                                   BigDecimal amount,
                                   String currency,
                                   PaymentProvider provider,
                                   String rejectionReason) {
        return new Payment(id, reference, amount, currency, PaymentStatus.REJECTED, provider, rejectionReason, Instant.now());
    }

    public PaymentId getId() {
        return id;
    }

    public PaymentReference getReference() {
        return reference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}
