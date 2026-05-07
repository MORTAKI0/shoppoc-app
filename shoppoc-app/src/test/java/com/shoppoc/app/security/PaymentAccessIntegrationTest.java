package com.shoppoc.app.security;

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

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void seedUsers() {
        springDataUserRepository.deleteAll();
        springDataUserRepository.save(new JpaUserEntity(
                UUID.randomUUID().toString(),
                "user@example.com",
                passwordEncoder.encode("Password123!"),
                new HashSet<UserRole>(Arrays.asList(UserRole.USER)),
                UserStatus.ACTIVE,
                Instant.now()
        ));
    }

    @Test
    void anonymousAuthorizeDenied() throws Exception {
        mockMvc.perform(post("/api/v1/payments/authorize")
                        .contentType("application/json")
                        .content("{\"amount\":99.99,\"currency\":\"EUR\",\"orderReference\":\"ORDER-DEMO-001\",\"paymentMethodToken\":\"stub-ok\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userCanAuthorizeAndGetPayment() throws Exception {
        String response = mockMvc.perform(post("/api/v1/payments/authorize")
                        .with(httpBasic("user@example.com", "Password123!"))
                        .contentType("application/json")
                        .content("{\"amount\":99.99,\"currency\":\"EUR\",\"orderReference\":\"ORDER-DEMO-001\",\"paymentMethodToken\":\"stub-ok\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("AUTHORIZED"))
                .andExpect(jsonPath("$.reference").exists())
                .andReturn().getResponse().getContentAsString();

        String paymentId = extractValue(response, "id");

        mockMvc.perform(get("/api/v1/payments/" + paymentId)
                        .with(httpBasic("user@example.com", "Password123!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId));
    }

    private String extractValue(String json, String key) {
        String needle = "\"" + key + "\":\"";
        int start = json.indexOf(needle);
        if (start < 0) {
            return "";
        }
        int valueStart = start + needle.length();
        int end = json.indexOf('"', valueStart);
        return json.substring(valueStart, end);
    }
}
