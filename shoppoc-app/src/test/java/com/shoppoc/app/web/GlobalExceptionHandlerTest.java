package com.shoppoc.app.web;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;
import com.shoppoc.shared.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldReturn400ForValidationError() throws Exception {
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").exists())
                .andExpect(jsonPath("$.message").value(not(containsString("Exception"))))
                .andExpect(content().string(not(containsString("java.lang"))))
                .andExpect(content().string(not(containsString("stackTrace"))));
    }

    @Test
    void shouldReturn409ForBusinessException() throws Exception {
        mockMvc.perform(get("/test/business"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Business rule failed"));
    }

    @Test
    void shouldReturn404ForNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn401ForAuthError() throws Exception {
        mockMvc.perform(get("/test/auth"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturn403ForAccessDenied() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @PostMapping("/validate")
        public String validate(@Valid @RequestBody ValidateRequest request) {
            return request.getName();
        }

        @GetMapping("/business")
        public String businessError() {
            throw new BusinessException(DomainError.of("BUSINESS_ERROR", "Business rule failed"));
        }

        @GetMapping("/not-found")
        public String notFound() {
            throw new NotFoundException("Resource not found");
        }

        @GetMapping("/auth")
        public String auth() {
            throw new BadCredentialsException("bad credentials");
        }

        @GetMapping("/forbidden")
        public String forbidden() {
            throw new AccessDeniedException("denied");
        }
    }

    static class ValidateRequest {

        @NotBlank
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
