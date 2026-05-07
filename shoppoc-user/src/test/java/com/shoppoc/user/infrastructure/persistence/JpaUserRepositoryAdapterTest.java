package com.shoppoc.user.infrastructure.persistence;

import com.shoppoc.user.UserTestApplication;
import com.shoppoc.user.domain.User;
import com.shoppoc.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = UserTestApplication.class)
@Import(JpaUserRepositoryAdapter.class)
class JpaUserRepositoryAdapterTest {

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @Autowired
    private JpaUserRepositoryAdapter adapter;

    @Test
    void saveAndFindByEmailPersistRoleAndStatus() {
        JpaUserEntity entity = new JpaUserEntity(
                "f4967a97-4df0-4981-a4f7-0e19385fd43a",
                "test@example.com",
                "$2a$10$3Ly7.B8fcdQxj6KfR6K9IuNAGCYf14fucv3slgbKrAA6iCMicnWG2",
                new HashSet<com.shoppoc.user.domain.UserRole>(Arrays.asList(com.shoppoc.user.domain.UserRole.USER)),
                UserStatus.ACTIVE,
                Instant.now()
        );
        springDataUserRepository.save(entity);

        Optional<User> loaded = adapter.findByEmail(com.shoppoc.user.domain.EmailAddress.of("TEST@example.com"));

        assertTrue(loaded.isPresent());
        assertEquals("test@example.com", loaded.get().getEmail().getValue());
        assertTrue(loaded.get().getRoles().contains(com.shoppoc.user.domain.UserRole.USER));
        assertEquals(UserStatus.ACTIVE, loaded.get().getStatus());
    }
}
