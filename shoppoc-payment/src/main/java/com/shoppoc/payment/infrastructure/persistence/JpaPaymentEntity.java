package com.shoppoc.payment.infrastructure.persistence;

import com.shoppoc.payment.domain.PaymentProvider;
import com.shoppoc.payment.domain.PaymentStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class JpaPaymentEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String reference;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider provider;

    @Column
    private String rejectionReason;

    @Column(nullable = false)
    private Instant processedAt;

    protected JpaPaymentEntity() {
    }

    public JpaPaymentEntity(String id,
                            String reference,
                            BigDecimal amount,
                            String currency,
                            PaymentStatus status,
                            PaymentProvider provider,
                            String rejectionReason,
                            Instant processedAt) {
        this.id = id;
        this.reference = reference;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.provider = provider;
        this.rejectionReason = rejectionReason;
        this.processedAt = processedAt;
    }

    public String getId() {
        return id;
    }

    public String getReference() {
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
