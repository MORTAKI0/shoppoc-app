package com.shoppoc.user.infrastructure.config;

import com.shoppoc.user.application.UserApplicationService;
import com.shoppoc.user.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserModuleConfig {

    @Bean
    public UserApplicationService userApplicationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserApplicationService(userRepository, passwordEncoder);
    }
}
