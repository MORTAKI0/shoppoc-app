package com.shoppoc.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .headers().frameOptions().sameOrigin()
            .and()
            .authorizeRequests()
                .antMatchers(
                    "/actuator/health",
                    "/actuator/info",
                    "/h2-console/**",
                    "/api/v1/products",
                    "/api/v1/products/**"
                ).permitAll()
                .anyRequest().authenticated()
            .and()
            .httpBasic();

        return http.build();
    }
}
