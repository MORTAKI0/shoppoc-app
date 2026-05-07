package com.shoppoc.user.infrastructure.config;

import com.shoppoc.user.domain.EmailAddress;
import com.shoppoc.user.domain.PasswordHash;
import com.shoppoc.user.domain.User;
import com.shoppoc.user.domain.UserRepository;
import com.shoppoc.user.domain.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;

@Configuration
@Profile("local")
public class UserLocalDataSeeder {

    @Bean
    public CommandLineRunner userSeedRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.findByEmail(EmailAddress.of("user@example.com")).isPresent()) {
                userRepository.save(User.register(
                        EmailAddress.of("user@example.com"),
                        PasswordHash.of(passwordEncoder.encode("Password123!"))
                ));
            }

            if (!userRepository.findByEmail(EmailAddress.of("admin@example.com")).isPresent()) {
                userRepository.save(new User(
                        com.shoppoc.user.domain.UserId.newId(),
                        EmailAddress.of("admin@example.com"),
                        PasswordHash.of(passwordEncoder.encode("Admin123!")),
                        new HashSet<UserRole>(Arrays.asList(UserRole.ADMIN)),
                        com.shoppoc.user.domain.UserStatus.ACTIVE,
                        Instant.now()
                ));
            }
        };
    }
}
