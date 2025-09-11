package org.csbf.security.service;

import org.csbf.security.model.EmailVerificationTokenEntity;
import org.csbf.security.utils.helperclasses.HelperDto;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface AuthenticationService {
    HelperDto.AuthenticationResponse register(HelperDto.RegisterRequest request);
    HelperDto.AuthenticationResponse authenticate(HelperDto.AuthenticationRequest request);
    String createEmailVerificationToken();
    EmailVerificationTokenEntity getEmailVerificationToken(String VerificationToken);
    HelperDto.ConfirmEmailResponse confirmEmail(String email, String token);
}
