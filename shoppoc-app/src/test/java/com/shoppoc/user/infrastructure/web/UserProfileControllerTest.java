package com.shoppoc.user.infrastructure.web;

import com.shoppoc.app.security.SecurityConfig;
import com.shoppoc.app.web.GlobalExceptionHandler;
import com.shoppoc.shared.error.NotFoundException;
import com.shoppoc.user.api.UserProfileDto;
import com.shoppoc.user.application.GetCurrentUserProfileUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserProfileController.class)
@AutoConfigureMockMvc
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetCurrentUserProfileUseCase getCurrentUserProfileUseCase;

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void userCanAccessProfile() throws Exception {
        when(getCurrentUserProfileUseCase.getCurrentUserProfile("user@example.com"))
                .thenReturn(new UserProfileDto("user-id", "user@example.com", Collections.singleton("USER"), "ACTIVE"));

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.roles[0]").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCanAccessProfile() throws Exception {
        when(getCurrentUserProfileUseCase.getCurrentUserProfile("admin@example.com"))
                .thenReturn(new UserProfileDto("admin-id", "admin@example.com", Collections.singleton("ADMIN"), "ACTIVE"));

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"));
    }

    @Test
    void anonymousIsDenied() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "missing@example.com", roles = "USER")
    void missingUserReturnsNotFound() throws Exception {
        when(getCurrentUserProfileUseCase.getCurrentUserProfile("missing@example.com"))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}
