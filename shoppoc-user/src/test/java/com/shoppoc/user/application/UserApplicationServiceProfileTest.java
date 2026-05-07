package com.shoppoc.user.application;

import com.shoppoc.shared.error.NotFoundException;
import com.shoppoc.user.api.UserProfileDto;
import com.shoppoc.user.domain.EmailAddress;
import com.shoppoc.user.domain.PasswordHash;
import com.shoppoc.user.domain.User;
import com.shoppoc.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserApplicationServiceProfileTest {

    @Test
    void getCurrentUserProfileReturnsProfileByEmail() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        User saved = User.register(EmailAddress.of("user@example.com"), PasswordHash.of(new BCryptPasswordEncoder().encode("Password123!")));
        repository.save(saved);
        UserApplicationService service = new UserApplicationService(repository, new BCryptPasswordEncoder());

        UserProfileDto result = service.getCurrentUserProfile("USER@example.com");

        assertEquals(saved.getId().getValue(), result.getId());
        assertEquals("user@example.com", result.getEmail());
        assertTrue(result.getRoles().contains("USER"));
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void getCurrentUserProfileThrowsWhenMissing() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserApplicationService service = new UserApplicationService(repository, new BCryptPasswordEncoder());

        assertThrows(NotFoundException.class, () -> service.getCurrentUserProfile("missing@example.com"));
    }

    @Test
    void userProfileDtoNeverExposesPasswordHash() {
        Method[] methods = UserProfileDto.class.getDeclaredMethods();
        boolean hasPasswordFieldGetter = false;
        for (Method method : methods) {
            if ("getPasswordHash".equals(method.getName()) || "getPassword".equals(method.getName())) {
                hasPasswordFieldGetter = true;
            }
        }
        assertFalse(hasPasswordFieldGetter);
    }

    private static class InMemoryUserRepository implements UserRepository {

        private final Map<String, User> users = new HashMap<String, User>();

        @Override
        public Optional<User> findByEmail(EmailAddress email) {
            return Optional.ofNullable(users.get(email.getValue()));
        }

        @Override
        public User save(User user) {
            users.put(user.getEmail().getValue(), user);
            return user;
        }
    }
}
