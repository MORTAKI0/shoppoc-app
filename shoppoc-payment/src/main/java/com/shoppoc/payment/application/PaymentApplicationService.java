package com.shoppoc.payment.application;

import com.shoppoc.payment.api.PaymentAuthorizationPort;
import com.shoppoc.payment.api.PaymentAuthorizationRequest;
import com.shoppoc.payment.api.PaymentDto;
import com.shoppoc.payment.domain.Payment;
import com.shoppoc.payment.domain.PaymentId;
import com.shoppoc.payment.domain.PaymentRepository;
import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;
import com.shoppoc.shared.error.NotFoundException;

import java.math.BigDecimal;
import java.util.Locale;

public class PaymentApplicationService implements AuthorizePaymentUseCase, GetPaymentStatusUseCase, PaymentAuthorizationPort {

    private final PaymentRepository paymentRepository;
    private final LocalPaymentProvider localPaymentProvider;

    public PaymentApplicationService(PaymentRepository paymentRepository,
                                     LocalPaymentProvider localPaymentProvider) {
        this.paymentRepository = paymentRepository;
        this.localPaymentProvider = localPaymentProvider;
    }

    @Override
    public PaymentDto authorize(AuthorizePaymentCommand command) {
        validate(command.getAmount(), command.getCurrency());

        PaymentId paymentId = PaymentId.newId();
        Payment payment = localPaymentProvider.authorize(
                paymentId,
                command.getAmount(),
                command.getCurrency().toUpperCase(Locale.ROOT),
                command.getOrderReference(),
                command.getPaymentMethodToken()
        ).getPayment();

        Payment saved = paymentRepository.save(payment);
        return toDto(saved);
    }

    @Override
    public PaymentDto authorize(PaymentAuthorizationRequest request) {
        return authorize(new AuthorizePaymentCommand(
                request.getAmount(),
                request.getCurrency(),
                request.getOrderReference(),
                request.getPaymentMethodToken()
        ));
    }

    @Override
    public PaymentDto getPayment(String paymentId) {
        PaymentId id = PaymentId.fromString(paymentId);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(new java.util.function.Supplier<NotFoundException>() {
                    @Override
                    public NotFoundException get() {
                        return new NotFoundException("Payment not found: " + paymentId);
                    }
                });
        return toDto(payment);
    }

    private void validate(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new BusinessException(DomainError.validation("Payment amount must not be null"));
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(DomainError.validation("Payment amount must be greater than zero"));
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new BusinessException(DomainError.validation("Payment currency must not be blank"));
        }
    }

    private PaymentDto toDto(Payment payment) {
        return new PaymentDto(
                payment.getId().value(),
                payment.getReference().value(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus().name(),
                payment.getProvider().name(),
                payment.getRejectionReason()
        );
    }
}
