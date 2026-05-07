package com.shoppoc.user.infrastructure.persistence;

import com.shoppoc.user.domain.EmailAddress;
import com.shoppoc.user.domain.PasswordHash;
import com.shoppoc.user.domain.User;
import com.shoppoc.user.domain.UserId;
import com.shoppoc.user.domain.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;

    public JpaUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return springDataUserRepository.findByEmail(email.getValue()).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        JpaUserEntity saved = springDataUserRepository.save(toEntity(user));
        return toDomain(saved);
    }

    private User toDomain(JpaUserEntity entity) {
        return new User(
                UserId.fromString(entity.getId()),
                EmailAddress.of(entity.getEmail()),
                PasswordHash.of(entity.getPasswordHash()),
                new HashSet<com.shoppoc.user.domain.UserRole>(entity.getRoles()),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private JpaUserEntity toEntity(User user) {
        return new JpaUserEntity(
                user.getId().getValue(),
                user.getEmail().getValue(),
                user.getPasswordHash().getValue(),
                new HashSet<com.shoppoc.user.domain.UserRole>(user.getRoles()),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
