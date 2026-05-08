package com.shoppoc.app.security;

import com.shoppoc.catalog.domain.ProductStatus;
import com.shoppoc.catalog.infrastructure.persistence.JpaProductEntity;
import com.shoppoc.catalog.infrastructure.persistence.SpringDataProductRepository;
import com.shoppoc.order.notification.domain.NotificationType;
import com.shoppoc.order.notification.infrastructure.persistence.SpringDataNotificationRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationRecordingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @Autowired
    private SpringDataProductRepository springDataProductRepository;

    @Autowired
    private SpringDataNotificationRepository springDataNotificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String productId;

    @BeforeEach
    void setup() {
        springDataNotificationRepository.deleteAll();
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
                "SKU-NOTIFY-1",
                "Phone",
                "Notification test product",
                new BigDecimal("10.00"),
                "EUR",
                50,
                ProductStatus.ACTIVE
        ));
    }

    @Test
    void userCreateOrderAuthorizedRecordsNotification() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .with(httpBasic("user@example.com", "Password123!"))
                        .contentType("application/json")
                        .content("{\"paymentMethodToken\":\"stub-ok\",\"lines\":[{\"productId\":\"" + productId + "\",\"quantity\":1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paymentStatus").value("AUTHORIZED"));

        assertEquals(1, springDataNotificationRepository.findAll().size());
        assertEquals(NotificationType.ORDER_PAYMENT_AUTHORIZED, springDataNotificationRepository.findAll().get(0).getType());
        assertEquals("RECORDED", springDataNotificationRepository.findAll().get(0).getStatus().name());
        assertFalse(springDataNotificationRepository.findAll().get(0).getSubject().trim().isEmpty());
        assertFalse(springDataNotificationRepository.findAll().get(0).getBody().trim().isEmpty());
        assertTrue(springDataNotificationRepository.findAll().get(0).getCreatedAt() != null);
    }

    @Test
    void userCreateOrderRejectedRecordsNotification() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .with(httpBasic("user@example.com", "Password123!"))
                        .contentType("application/json")
                        .content("{\"paymentMethodToken\":\"reject\",\"lines\":[{\"productId\":\"" + productId + "\",\"quantity\":1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAYMENT_REJECTED"))
                .andExpect(jsonPath("$.paymentStatus").value("REJECTED"));

        assertEquals(1, springDataNotificationRepository.findAll().size());
        assertEquals(NotificationType.ORDER_PAYMENT_REJECTED, springDataNotificationRepository.findAll().get(0).getType());
        assertTrue(springDataNotificationRepository.findAll().get(0).getBody().contains("Reason:"));
        assertEquals("RECORDED", springDataNotificationRepository.findAll().get(0).getStatus().name());
    }

    @Test
    void anonymousCreateOrderUnauthorizedAndNoNotification() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content("{\"paymentMethodToken\":\"stub-ok\",\"lines\":[{\"productId\":\"" + productId + "\",\"quantity\":1}]}"))
                .andExpect(status().isUnauthorized());

        assertTrue(springDataNotificationRepository.findAll().isEmpty());
    }
}
