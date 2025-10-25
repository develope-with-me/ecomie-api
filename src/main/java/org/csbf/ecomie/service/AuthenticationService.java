package org.csbf.ecomie.service;

import org.csbf.ecomie.entity.EmailVerificationTokenEntity;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;


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
