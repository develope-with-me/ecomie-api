package org.csbf.security.repository;

import org.csbf.security.model.EmailVerificationToken;
import org.csbf.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

        EmailVerificationToken findByToken(String token);

        EmailVerificationToken findByUser(User user);
}
