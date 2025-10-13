package org.csbf.security.repository;

import org.csbf.security.entity.EmailVerificationTokenEntity;
import org.csbf.security.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, Long> {

        EmailVerificationTokenEntity findByToken(String token);

        EmailVerificationTokenEntity findByUser(UserEntity userEntity);
}
