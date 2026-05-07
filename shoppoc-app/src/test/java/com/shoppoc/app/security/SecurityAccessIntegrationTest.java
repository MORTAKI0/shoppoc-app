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

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAccessIntegrationTest {

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
    void publicEndpointsAreAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/products")).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType("application/json")
                        .content("{\"email\":\"new@example.com\",\"password\":\"Password123!\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void userEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/users/me").with(httpBasic("user@example.com", "Password123!")))
                .andExpect(status().isOk());
    }

    @Test
    void adminPathEnforcesRoleAndAllowsAdminCreation() throws Exception {
        String body = "{\"sku\":\"SKU-AUTH-001\",\"name\":\"Name\",\"description\":\"Desc\",\"priceAmount\":10.00,\"priceCurrency\":\"USD\",\"stockQuantity\":1}";

        mockMvc.perform(post("/api/v1/admin/products").contentType("application/json").content(body))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/admin/products").with(httpBasic("user@example.com", "Password123!")).contentType("application/json").content(body))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/admin/products").with(httpBasic("admin@example.com", "Admin123!")).contentType("application/json").content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].sku", hasItem("SKU-AUTH-001")));
    }
}
