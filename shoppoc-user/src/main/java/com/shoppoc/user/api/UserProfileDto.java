package com.shoppoc.user.api;

import java.util.Set;

public class UserProfileDto {

    private final String id;
    private final String email;
    private final Set<String> roles;
    private final String status;

    public UserProfileDto(String id, String email, Set<String> roles, String status) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.status = status;
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
