package com.shoppoc.payment.infrastructure.config;

import com.shoppoc.payment.application.LocalPaymentProvider;
import com.shoppoc.payment.application.PaymentApplicationService;
import com.shoppoc.payment.domain.PaymentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentModuleConfig {

    @Bean
    public LocalPaymentProvider localPaymentProvider() {
        return new LocalPaymentProvider();
    }

    @Bean
    public PaymentApplicationService paymentApplicationService(PaymentRepository paymentRepository,
                                                               LocalPaymentProvider localPaymentProvider) {
        return new PaymentApplicationService(paymentRepository, localPaymentProvider);
    }
}
