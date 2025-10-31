package org.csbf.ecomie.repository;

import org.csbf.ecomie.entity.UserTokenEntity;
import org.csbf.ecomie.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface UserTokenRepository extends JpaRepository<UserTokenEntity, UUID> {

        Optional<UserTokenEntity> findByToken(String token);

        Optional<UserTokenEntity> findByTokenAndUser_Id(String token, UUID userId);

        List<UserTokenEntity> findByUser_Id(UUID userId);

        Optional<UserTokenEntity> findByTokenAndUser_Email(String token, String userEmail);
}
