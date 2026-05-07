package com.shoppoc.user.infrastructure.web;

import com.shoppoc.user.api.UserProfileDto;
import com.shoppoc.user.application.GetCurrentUserProfileUseCase;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserProfileController {

    private final GetCurrentUserProfileUseCase getCurrentUserProfileUseCase;

    public UserProfileController(GetCurrentUserProfileUseCase getCurrentUserProfileUseCase) {
        this.getCurrentUserProfileUseCase = getCurrentUserProfileUseCase;
    }

    @GetMapping("/me")
    public UserProfileResponse me(Authentication authentication) {
        UserProfileDto profile = getCurrentUserProfileUseCase.getCurrentUserProfile(authentication.getName());
        return UserProfileResponse.from(profile);
    }
}
