package com.shoppoc.order.infrastructure.web;

import com.shoppoc.app.security.SecurityConfig;
import com.shoppoc.app.web.GlobalExceptionHandler;
import com.shoppoc.order.api.OrderDto;
import com.shoppoc.order.api.OrderLineDto;
import com.shoppoc.order.application.CreateOrderUseCase;
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
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void userCanCreateOrder() throws Exception {
        when(createOrderUseCase.createOrder(any())).thenReturn(sampleDto("user@example.com"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lines\":[{\"productId\":\"p1\",\"quantity\":2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(20.00))
                .andExpect(jsonPath("$.lines[0].sku").value("SKU-1"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCanCreateOrder() throws Exception {
        when(createOrderUseCase.createOrder(any())).thenReturn(sampleDto("admin@example.com"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lines\":[{\"productId\":\"p1\",\"quantity\":2}]}"))
                .andExpect(status().isCreated());
    }

    @Test
    void anonymousCannotCreateOrder() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lines\":[{\"productId\":\"p1\",\"quantity\":2}]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void invalidRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lines\":[{\"productId\":\"\",\"quantity\":0}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void invalidProductReturnsNotFound() throws Exception {
        when(createOrderUseCase.createOrder(any())).thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lines\":[{\"productId\":\"missing\",\"quantity\":1}]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    private OrderDto sampleDto(String email) {
        return new OrderDto(
                "order-1",
                email,
                "CREATED",
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
