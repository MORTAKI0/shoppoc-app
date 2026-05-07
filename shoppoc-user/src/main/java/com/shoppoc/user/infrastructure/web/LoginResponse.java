package com.shoppoc.user.infrastructure.web;

import java.util.Set;

public class LoginResponse {

    private final String email;
    private final Set<String> roles;
    private final boolean authenticated;

    public LoginResponse(String email, Set<String> roles, boolean authenticated) {
        this.email = email;
        this.roles = roles;
        this.authenticated = authenticated;
    }

    public String getEmail() { return email; }
    public Set<String> getRoles() { return roles; }
    public boolean isAuthenticated() { return authenticated; }
}
