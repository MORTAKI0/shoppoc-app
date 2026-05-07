package com.shoppoc.user.application;

public class RegisterUserCommand {

    private final String email;
    private final String password;

    public RegisterUserCommand(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
