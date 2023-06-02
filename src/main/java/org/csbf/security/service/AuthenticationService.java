package org.csbf.security.service;

import org.csbf.security.model.EmailVerificationToken;
import org.csbf.security.utils.helperclasses.HelperDto;

public interface AuthenticationService {
    HelperDto.AuthenticationResponse register(HelperDto.RegisterRequest request);
    HelperDto.AuthenticationResponse authenticate(HelperDto.AuthenticationRequest request);
    String createEmailVerificationToken();
    EmailVerificationToken getEmailVerificationToken(String VerificationToken);
    HelperDto.ConfirmEmailResponse confirmEmail(String email, String token);
}
