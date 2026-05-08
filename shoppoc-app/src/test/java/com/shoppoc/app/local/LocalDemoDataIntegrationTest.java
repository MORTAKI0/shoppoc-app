package com.shoppoc.app.local;

import com.shoppoc.catalog.infrastructure.persistence.JpaProductEntity;
import com.shoppoc.catalog.infrastructure.persistence.SpringDataProductRepository;
import com.shoppoc.user.infrastructure.persistence.JpaUserEntity;
import com.shoppoc.user.infrastructure.persistence.SpringDataUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("local")
class LocalDemoDataIntegrationTest {

    @Autowired
    private SpringDataUserRepository userRepository;

    @Autowired
    private SpringDataProductRepository productRepository;

    @Test
    void localProfileSeedsDemoUsersAndProducts() {
        Optional<JpaUserEntity> user = userRepository.findByEmail("user@example.com");
        Optional<JpaUserEntity> admin = userRepository.findByEmail("admin@example.com");

        assertTrue(user.isPresent());
        assertTrue(admin.isPresent());

        assertEquals("ACTIVE", user.get().getStatus().name());
        assertEquals("ACTIVE", admin.get().getStatus().name());
        assertTrue(user.get().getRoles().stream().anyMatch(role -> "USER".equals(role.name())));
        assertTrue(admin.get().getRoles().stream().anyMatch(role -> "ADMIN".equals(role.name())));

        assertFalse("Password123!".equals(user.get().getPasswordHash()));
        assertFalse("Admin123!".equals(admin.get().getPasswordHash()));
        assertTrue(user.get().getPasswordHash().startsWith("$2"));
        assertTrue(admin.get().getPasswordHash().startsWith("$2"));

        assertProductExists("SKU-LAPTOP-001");
        assertProductExists("SKU-HEADSET-001");
        assertProductExists("SKU-KEYBOARD-001");
    }

    private void assertProductExists(String sku) {
        Optional<JpaProductEntity> product = productRepository.findBySku(sku);
        assertTrue(product.isPresent());
    }
}
