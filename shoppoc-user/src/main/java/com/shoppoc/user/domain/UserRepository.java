package com.shoppoc.user.domain;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(EmailAddress email);

    User save(User user);
}
