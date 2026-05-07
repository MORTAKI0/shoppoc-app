package com.shoppoc.user.domain;

import com.shoppoc.shared.error.BusinessException;
import com.shoppoc.shared.error.DomainError;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class User {

    private final UserId id;
    private final EmailAddress email;
    private final PasswordHash passwordHash;
    private final Set<UserRole> roles;
    private final UserStatus status;
    private final Instant createdAt;

    public User(UserId id,
                EmailAddress email,
                PasswordHash passwordHash,
                Set<UserRole> roles,
                UserStatus status,
                Instant createdAt) {
        if (id == null) {
            throw new BusinessException(DomainError.validation("User id must not be null"));
        }
        if (email == null) {
            throw new BusinessException(DomainError.validation("Email must not be null"));
        }
        if (passwordHash == null) {
            throw new BusinessException(DomainError.validation("Password hash must not be null"));
        }
        if (roles == null || roles.isEmpty()) {
            throw new BusinessException(DomainError.validation("User must have at least one role"));
        }
        if (status == null) {
            throw new BusinessException(DomainError.validation("Status must not be null"));
        }
        if (createdAt == null) {
            throw new BusinessException(DomainError.validation("CreatedAt must not be null"));
        }
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = Collections.unmodifiableSet(new HashSet<UserRole>(roles));
        this.status = status;
        this.createdAt = createdAt;
    }

    public static User register(EmailAddress email, PasswordHash passwordHash) {
        Set<UserRole> defaultRoles = new HashSet<UserRole>();
        defaultRoles.add(UserRole.USER);
        return new User(
                UserId.newId(),
                email,
                passwordHash,
                defaultRoles,
                UserStatus.ACTIVE,
                Instant.now()
        );
    }

    public UserId getId() { return id; }
    public EmailAddress getEmail() { return email; }
    public PasswordHash getPasswordHash() { return passwordHash; }
    public Set<UserRole> getRoles() { return roles; }
    public UserStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
