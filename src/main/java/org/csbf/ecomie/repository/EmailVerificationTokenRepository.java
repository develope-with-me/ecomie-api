package org.csbf.ecomie.repository;

import org.csbf.ecomie.entity.EmailVerificationTokenEntity;
import org.csbf.ecomie.entity.UserEntity;
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
