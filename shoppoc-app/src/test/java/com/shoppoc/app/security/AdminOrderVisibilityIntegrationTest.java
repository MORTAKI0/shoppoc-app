package com.shoppoc.app.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;

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
class AdminOrderVisibilityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

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
        springDataUserRepository.save(new JpaUserEntity(
                UUID.randomUUID().toString(),
                "user2@example.com",
                passwordEncoder.encode("Password123!"),
                new HashSet<UserRole>(Arrays.asList(UserRole.USER)),
                UserStatus.ACTIVE,
                Instant.now()
        ));
        springDataUserRepository.save(new JpaUserEntity(
                UUID.randomUUID().toString(),
                "admin@example.com",
                passwordEncoder.encode("Admin123!"),
                new HashSet<UserRole>(Arrays.asList(UserRole.ADMIN)),
                UserStatus.ACTIVE,
                Instant.now()
        ));
    }

    @Test
    void adminCanSeeAllOrdersWhileUserAndAnonymousCannotAccessAdminList() throws Exception {
        String productId = createProductAndGetId();

        createOrder("user@example.com", "Password123!", productId);
        createOrder("user2@example.com", "Password123!", productId);

        mockMvc.perform(get("/api/v1/admin/orders").with(httpBasic("admin@example.com", "Admin123!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].customerEmail").isNotEmpty())
                .andExpect(jsonPath("$[0].status").isNotEmpty())
                .andExpect(jsonPath("$[0].totalAmount").exists())
                .andExpect(jsonPath("$[0].totalCurrency").isNotEmpty())
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty());

        mockMvc.perform(get("/api/v1/admin/orders").with(httpBasic("user@example.com", "Password123!")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/admin/orders"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/orders").with(httpBasic("user@example.com", "Password123!")))
                .andExpect(status().isOk());
    }

    private String createProductAndGetId() throws Exception {
        mockMvc.perform(post("/api/v1/admin/products")
                        .with(httpBasic("admin@example.com", "Admin123!"))
                        .contentType("application/json")
                        .content("{\"sku\":\"SKU-EGA340-1\",\"name\":\"EGA340 Product\",\"description\":\"Admin order visibility test product\",\"priceAmount\":25.00,\"priceCurrency\":\"EUR\",\"stockQuantity\":10}"))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode products = objectMapper.readTree(result.getResponse().getContentAsString());
        return products.get(products.size() - 1).get("id").asText();
    }

    private void createOrder(String email, String password, String productId) throws Exception {
        String requestBody = "{\"paymentMethodToken\":\"stub-ok\",\"lines\":[{\"productId\":\"" + productId + "\",\"quantity\":1}]}";
        mockMvc.perform(post("/api/v1/orders")
                        .with(httpBasic(email, password))
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());
    }
}
