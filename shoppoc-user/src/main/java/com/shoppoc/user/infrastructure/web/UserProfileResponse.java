package com.shoppoc.user.infrastructure.web;

import com.shoppoc.user.api.UserProfileDto;

import java.util.Set;

public class UserProfileResponse {

    private final String id;
    private final String email;
    private final Set<String> roles;
    private final String status;

    public UserProfileResponse(String id, String email, Set<String> roles, String status) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.status = status;
    }

    public static UserProfileResponse from(UserProfileDto dto) {
        return new UserProfileResponse(dto.getId(), dto.getEmail(), dto.getRoles(), dto.getStatus());
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getStatus() {
        return status;
    }
}
