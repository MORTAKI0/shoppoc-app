package com.shoppoc.app.security;

import com.shoppoc.catalog.domain.ProductStatus;
import com.shoppoc.catalog.infrastructure.persistence.JpaProductEntity;
import com.shoppoc.catalog.infrastructure.persistence.SpringDataProductRepository;
import com.shoppoc.user.domain.UserRole;
import com.shoppoc.user.domain.UserStatus;
import com.shoppoc.user.infrastructure.persistence.JpaUserEntity;
import com.shoppoc.user.infrastructure.persistence.SpringDataUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderPaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @Autowired
    private SpringDataProductRepository springDataProductRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String productId;

    @BeforeEach
    void setup() {
        springDataUserRepository.deleteAll();
        springDataProductRepository.deleteAll();

        springDataUserRepository.save(new JpaUserEntity(
                UUID.randomUUID().toString(),
                "user@example.com",
                passwordEncoder.encode("Password123!"),
                new HashSet<UserRole>(Arrays.asList(UserRole.USER)),
                UserStatus.ACTIVE,
                Instant.now()
        ));

        productId = UUID.randomUUID().toString();
        springDataProductRepository.save(new JpaProductEntity(
                productId,
                "SKU-ORDER-1",
                "Phone",
                "Test product",
                new BigDecimal("10.00"),
                "EUR",
                50,
                ProductStatus.ACTIVE
        ));
    }

    @Test
    void userCreateOrderAuthorized() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .with(httpBasic("user@example.com", "Password123!"))
                        .contentType("application/json")
                        .content("{\"paymentMethodToken\":\"stub-ok\",\"lines\":[{\"productId\":\"" + productId + "\",\"quantity\":2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paymentStatus").value("AUTHORIZED"))
                .andExpect(jsonPath("$.paymentId").exists())
                .andExpect(jsonPath("$.paymentReference").exists())
                .andExpect(jsonPath("$.totalAmount").value(20.00));
    }

    @Test
    void userCreateOrderRejected() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .with(httpBasic("user@example.com", "Password123!"))
                        .contentType("application/json")
                        .content("{\"paymentMethodToken\":\"reject\",\"lines\":[{\"productId\":\"" + productId + "\",\"quantity\":1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAYMENT_REJECTED"))
                .andExpect(jsonPath("$.paymentStatus").value("REJECTED"))
                .andExpect(jsonPath("$.paymentRejectionReason").exists());
    }

    @Test
    void anonymousCreateOrderUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content("{\"paymentMethodToken\":\"stub-ok\",\"lines\":[{\"productId\":\"" + productId + "\",\"quantity\":1}]}"))
                .andExpect(status().isUnauthorized());
    }
}
