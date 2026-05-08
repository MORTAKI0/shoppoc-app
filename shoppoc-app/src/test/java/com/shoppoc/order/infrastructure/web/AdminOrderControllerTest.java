package com.shoppoc.order.infrastructure.web;

import com.shoppoc.app.security.SecurityConfig;
import com.shoppoc.app.web.GlobalExceptionHandler;
import com.shoppoc.order.api.AdminOrderSummaryDto;
import com.shoppoc.order.application.AdminListOrdersUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminOrderController.class)
@AutoConfigureMockMvc
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class AdminOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminListOrdersUseCase adminListOrdersUseCase;

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCanListAllOrders() throws Exception {
        when(adminListOrdersUseCase.listAllOrders()).thenReturn(Arrays.asList(
                sample("order-1", "user-a@example.com", "PAID", "2026-01-01T10:00:00Z"),
                sample("order-2", "user-b@example.com", "CREATED", "2026-01-02T10:00:00Z")
        ));

        mockMvc.perform(get("/api/v1/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("order-1"))
                .andExpect(jsonPath("$[0].customerEmail").value("user-a@example.com"))
                .andExpect(jsonPath("$[0].totalAmount").value(20.00))
                .andExpect(jsonPath("$[0].totalCurrency").value("EUR"))
                .andExpect(jsonPath("$[0].status").value("PAID"))
                .andExpect(jsonPath("$[0].createdAt").value("2026-01-01T10:00:00Z"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void userGetsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousGetsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/admin/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminGetsEmptyArray() throws Exception {
        when(adminListOrdersUseCase.listAllOrders()).thenReturn(Collections.<AdminOrderSummaryDto>emptyList());

        mockMvc.perform(get("/api/v1/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private AdminOrderSummaryDto sample(String id, String customerEmail, String status, String createdAt) {
        return new AdminOrderSummaryDto(
                id,
                customerEmail,
                status,
                new BigDecimal("20.00"),
                "EUR",
                createdAt,
                "AUTHORIZED",
                "pay-1",
                "ref-1"
        );
    }
}
