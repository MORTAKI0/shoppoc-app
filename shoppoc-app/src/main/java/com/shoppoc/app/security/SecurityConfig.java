package com.shoppoc.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                .antMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/admin/products").hasRole("ADMIN")
                .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                .antMatchers(HttpMethod.POST, "/api/v1/orders").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/v1/orders/**").authenticated()
                .antMatchers(HttpMethod.POST, "/api/v1/payments/authorize").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/payments/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            .and()
            .httpBasic();

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
