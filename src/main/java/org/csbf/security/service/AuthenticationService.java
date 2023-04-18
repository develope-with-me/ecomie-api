package org.csbf.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.csbf.security.model.EmailVerificationToken;
import org.csbf.security.model.User;
import org.csbf.security.payload.AuthenticationRequest;
import org.csbf.security.payload.AuthenticationResponse;
import org.csbf.security.payload.RegisterRequest;

import java.util.Map;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);
    void createEmailVerificationToken(User user, String token);

    EmailVerificationToken getEmailVerificationToken(String VerificationToken);
    User getUser(String verificationToken);
    Map confirmRegistration(HttpServletRequest servletRequest, String token);
}
