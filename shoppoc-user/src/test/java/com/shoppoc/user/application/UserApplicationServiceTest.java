package com.shoppoc.user.application;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.user.domain.EmailAddress;
import com.shoppoc.user.domain.PasswordHash;
import com.shoppoc.user.domain.User;
import com.shoppoc.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserApplicationServiceTest {

    @Test
    void registerHashesPasswordAndNormalizesEmail() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserApplicationService service = new UserApplicationService(repository, new BCryptPasswordEncoder());

        com.shoppoc.user.api.UserDto result = service.register(new RegisterUserCommand("USER@Example.Com", "Password123!"));

        User saved = repository.findByEmail(EmailAddress.of("user@example.com")).get();
        assertEquals("user@example.com", result.getEmail());
        assertTrue(saved.getPasswordHash().getValue().startsWith("$2"));
        assertFalse(saved.getPasswordHash().getValue().equals("Password123!"));
    }

    @Test
    void registerThrowsOnDuplicateEmail() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        User existing = User.register(EmailAddress.of("user@example.com"), PasswordHash.of(new BCryptPasswordEncoder().encode("Password123!")));
        repository.save(existing);

        UserApplicationService service = new UserApplicationService(repository, new BCryptPasswordEncoder());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.register(new RegisterUserCommand("user@example.com", "Password123!")));
        assertEquals(UserApplicationService.USER_EMAIL_ALREADY_EXISTS, exception.getDomainError().getCode());
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
