package com.shoppoc.payment.infrastructure.web;

import com.shoppoc.app.security.SecurityConfig;
import com.shoppoc.app.web.GlobalExceptionHandler;
import com.shoppoc.payment.api.PaymentDto;
import com.shoppoc.payment.application.AuthorizePaymentUseCase;
import com.shoppoc.payment.application.GetPaymentStatusUseCase;
import com.shoppoc.shared.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorizePaymentUseCase authorizePaymentUseCase;

    @MockBean
    private GetPaymentStatusUseCase getPaymentStatusUseCase;

    @Test
    @WithMockUser(roles = "USER")
    void userCanAuthorizePayment() throws Exception {
        when(authorizePaymentUseCase.authorize(any())).thenReturn(new PaymentDto(
                "pay-1",
                "LOCAL-1",
                new BigDecimal("99.99"),
                "EUR",
                "AUTHORIZED",
                "LOCAL_STUB",
                null
        ));

        mockMvc.perform(post("/api/v1/payments/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":99.99,\"currency\":\"EUR\",\"orderReference\":\"ORDER-1\",\"paymentMethodToken\":\"stub-ok\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AUTHORIZED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAuthorizePayment() throws Exception {
        when(authorizePaymentUseCase.authorize(any())).thenReturn(new PaymentDto(
                "pay-2",
                "LOCAL-2",
                new BigDecimal("10.00"),
                "EUR",
                "AUTHORIZED",
                "LOCAL_STUB",
                null
        ));

        mockMvc.perform(post("/api/v1/payments/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":10.00,\"currency\":\"EUR\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void anonymousCannotAuthorizePayment() throws Exception {
        mockMvc.perform(post("/api/v1/payments/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":99.99,\"currency\":\"EUR\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void invalidRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/payments/authorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":0,\"currency\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanGetPayment() throws Exception {
        when(getPaymentStatusUseCase.getPayment(anyString())).thenReturn(new PaymentDto(
                "pay-3",
                "LOCAL-3",
                new BigDecimal("40.00"),
                "EUR",
                "AUTHORIZED",
                "LOCAL_STUB",
                null
        ));

        mockMvc.perform(get("/api/v1/payments/pay-3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pay-3"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void missingPaymentReturnsNotFound() throws Exception {
        when(getPaymentStatusUseCase.getPayment(anyString())).thenThrow(new NotFoundException("Payment not found"));

        mockMvc.perform(get("/api/v1/payments/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}
