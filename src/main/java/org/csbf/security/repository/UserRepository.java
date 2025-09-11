package org.csbf.security.repository;

import org.csbf.security.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmailVerificationToken(String token);
    Optional<UserEntity> findByEmailAndEmailVerificationToken(String email, String token);

    boolean existsByEmail(String email);
}
