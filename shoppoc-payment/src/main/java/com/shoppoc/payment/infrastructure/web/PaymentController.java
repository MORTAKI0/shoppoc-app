package com.shoppoc.payment.infrastructure.web;

import com.shoppoc.payment.api.PaymentDto;
import com.shoppoc.payment.application.AuthorizePaymentCommand;
import com.shoppoc.payment.application.AuthorizePaymentUseCase;
import com.shoppoc.payment.application.GetPaymentStatusUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final AuthorizePaymentUseCase authorizePaymentUseCase;
    private final GetPaymentStatusUseCase getPaymentStatusUseCase;

    public PaymentController(AuthorizePaymentUseCase authorizePaymentUseCase,
                             GetPaymentStatusUseCase getPaymentStatusUseCase) {
        this.authorizePaymentUseCase = authorizePaymentUseCase;
        this.getPaymentStatusUseCase = getPaymentStatusUseCase;
    }

    @PostMapping("/authorize")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse authorize(@Valid @RequestBody AuthorizePaymentRequest request) {
        PaymentDto paymentDto = authorizePaymentUseCase.authorize(new AuthorizePaymentCommand(
                request.getAmount(),
                request.getCurrency(),
                request.getOrderReference(),
                request.getPaymentMethodToken()
        ));
        return PaymentResponse.fromDto(paymentDto);
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable String paymentId) {
        return PaymentResponse.fromDto(getPaymentStatusUseCase.getPayment(paymentId));
    }
}
