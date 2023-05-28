package org.csbf.security.repository;

import org.csbf.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailVerificationToken(String token);
    Optional<User> findByEmailAndEmailVerificationToken(String email, String token);

    boolean existsByEmail(String email);
}
