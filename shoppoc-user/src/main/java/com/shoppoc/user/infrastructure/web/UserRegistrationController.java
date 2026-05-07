package com.shoppoc.user.infrastructure.web;

import com.shoppoc.user.api.UserDto;
import com.shoppoc.user.application.RegisterUserCommand;
import com.shoppoc.user.application.RegisterUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserRegistrationController {

    private final RegisterUserUseCase registerUserUseCase;

    public UserRegistrationController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterUserRequest request) {
        UserDto userDto = registerUserUseCase.register(new RegisterUserCommand(request.getEmail(), request.getPassword()));
        return new UserResponse(userDto.getId(), userDto.getEmail(), userDto.getRoles(), userDto.getStatus());
    }
}
