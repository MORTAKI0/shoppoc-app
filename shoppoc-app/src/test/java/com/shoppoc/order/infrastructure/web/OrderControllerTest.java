package com.shoppoc.order.infrastructure.web;

import com.shoppoc.app.security.SecurityConfig;
import com.shoppoc.app.web.GlobalExceptionHandler;
import com.shoppoc.order.api.OrderDto;
import com.shoppoc.order.api.OrderLineDto;
import com.shoppoc.order.application.CreateOrderUseCase;
import com.shoppoc.order.application.GetOrderUseCase;
import com.shoppoc.order.application.ListCurrentUserOrdersUseCase;
import com.shoppoc.shared.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private GetOrderUseCase getOrderUseCase;

    @MockBean
    private ListCurrentUserOrdersUseCase listCurrentUserOrdersUseCase;

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void userCanCreatePaidOrder() throws Exception {
        when(createOrderUseCase.createOrder(any())).thenReturn(sampleDto("order-1", "user@example.com", "PAID", "AUTHORIZED", null));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentMethodToken\":\"stub-ok\",\"lines\":[{\"productId\":\"p1\",\"quantity\":2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paymentId").value("pay-1"))
                .andExpect(jsonPath("$.paymentReference").value("ref-1"))
                .andExpect(jsonPath("$.paymentStatus").value("AUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void userCanCreateRejectedOrder() throws Exception {
        when(createOrderUseCase.createOrder(any())).thenReturn(sampleDto("order-1", "user@example.com", "PAYMENT_REJECTED", "REJECTED", "Payment rejected by local stub token"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentMethodToken\":\"reject\",\"lines\":[{\"productId\":\"p1\",\"quantity\":2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAYMENT_REJECTED"))
                .andExpect(jsonPath("$.paymentStatus").value("REJECTED"))
                .andExpect(jsonPath("$.paymentRejectionReason").value("Payment rejected by local stub token"));
    }

    @Test
    void anonymousCannotCreateOrder() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentMethodToken\":\"stub-ok\",\"lines\":[{\"productId\":\"p1\",\"quantity\":2}]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void invalidRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentMethodToken\":\"\",\"lines\":[{\"productId\":\"\",\"quantity\":0}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void invalidProductReturnsNotFound() throws Exception {
        when(createOrderUseCase.createOrder(any())).thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentMethodToken\":\"stub-ok\",\"lines\":[{\"productId\":\"missing\",\"quantity\":1}]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void userCanGetOwnOrder() throws Exception {
        when(getOrderUseCase.getOrder(eq("order-1"), eq("user@example.com")))
                .thenReturn(sampleDto("order-1", "user@example.com", "PAID", "AUTHORIZED", null));

        mockMvc.perform(get("/api/v1/orders/order-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("order-1"))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.lines[0].productId").value("p1"))
                .andExpect(jsonPath("$.totalAmount").value(20.00))
                .andExpect(jsonPath("$.paymentStatus").value("AUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void userCanListOwnOrders() throws Exception {
        when(listCurrentUserOrdersUseCase.listCurrentUserOrders(eq("user@example.com")))
                .thenReturn(Arrays.asList(
                        sampleDto("order-1", "user@example.com", "PAID", "AUTHORIZED", null),
                        sampleDto("order-2", "user@example.com", "PAYMENT_REJECTED", "REJECTED", "Payment rejected by local stub token")
                ));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("order-1"))
                .andExpect(jsonPath("$[1].id").value("order-2"));
    }

    @Test
    void anonymousCannotGetOrder() throws Exception {
        mockMvc.perform(get("/api/v1/orders/order-1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anonymousCannotListOrders() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = "USER")
    void crossUserOrderAccessDenied() throws Exception {
        when(getOrderUseCase.getOrder(eq("order-1"), eq("other@example.com")))
                .thenThrow(new AccessDeniedException("Order access denied"));

        mockMvc.perform(get("/api/v1/orders/order-1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void missingOrderReturnsNotFound() throws Exception {
        when(getOrderUseCase.getOrder(eq("missing"), eq("user@example.com")))
                .thenThrow(new NotFoundException("Order not found"));

        mockMvc.perform(get("/api/v1/orders/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    private OrderDto sampleDto(String id, String email, String status, String paymentStatus, String rejectionReason) {
        return new OrderDto(
                id,
                email,
                status,
                "pay-1",
                "ref-1",
                paymentStatus,
                rejectionReason,
                new BigDecimal("20.00"),
                "EUR",
                Collections.singletonList(new OrderLineDto(
                        "p1",
                        "SKU-1",
                        "Phone",
                        2,
                        new BigDecimal("10.00"),
                        "EUR",
                        new BigDecimal("20.00"),
                        "EUR"
                ))
        );
    }
}
