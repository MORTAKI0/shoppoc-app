package com.shoppoc.user.application;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;
import com.shoppoc.shared.error.NotFoundException;
import com.shoppoc.user.api.UserDto;
import com.shoppoc.user.api.UserProfileDto;
import com.shoppoc.user.domain.EmailAddress;
import com.shoppoc.user.domain.PasswordHash;
import com.shoppoc.user.domain.User;
import com.shoppoc.user.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.stream.Collectors;

public class UserApplicationService implements RegisterUserUseCase, GetCurrentUserProfileUseCase {

    public static final String USER_EMAIL_ALREADY_EXISTS = "USER_EMAIL_ALREADY_EXISTS";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserApplicationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto register(RegisterUserCommand command) {
        EmailAddress email = EmailAddress.of(command.getEmail());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(DomainError.of(USER_EMAIL_ALREADY_EXISTS, "Email already exists"));
        }

        String hashedPassword = passwordEncoder.encode(command.getPassword());
        User savedUser = userRepository.save(User.register(email, PasswordHash.of(hashedPassword)));
        Set<String> roleNames = savedUser.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        return new UserDto(savedUser.getId().getValue(), savedUser.getEmail().getValue(), roleNames, savedUser.getStatus().name());
    }

    @Override
    public UserProfileDto getCurrentUserProfile(String email) {
        EmailAddress normalizedEmail = EmailAddress.of(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Set<String> roleNames = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        return new UserProfileDto(user.getId().getValue(), user.getEmail().getValue(), roleNames, user.getStatus().name());
    }
}
