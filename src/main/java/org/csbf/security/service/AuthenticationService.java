package org.csbf.security.service;

import org.csbf.security.entity.EmailVerificationTokenEntity;
import org.csbf.security.utils.helperclasses.HelperDomain.*;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    String createEmailVerificationToken();
    EmailVerificationTokenEntity getEmailVerificationToken(String VerificationToken);
    ConfirmEmailResponse confirmEmail(String email, String token);
}
