package com.shoppoc.user.application;

import com.shoppoc.user.api.UserProfileDto;

public interface GetCurrentUserProfileUseCase {

    UserProfileDto getCurrentUserProfile(String email);
}
