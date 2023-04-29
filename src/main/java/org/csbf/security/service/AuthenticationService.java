package org.csbf.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.csbf.security.model.EmailVerificationToken;
import org.csbf.security.model.User;
import org.csbf.security.payload.AuthenticationRequest;
import org.csbf.security.payload.AuthenticationResponse;
import org.csbf.security.payload.RegisterRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);
    String createEmailVerificationToken();
    EmailVerificationToken getEmailVerificationToken(String VerificationToken);
    Map confirmEmail(String email, String token) throws UnsupportedEncodingException;
}
