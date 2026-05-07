package com.shoppoc.user.application;

import com.shoppoc.user.api.UserDto;

public interface RegisterUserUseCase {

    UserDto register(RegisterUserCommand command);
}
