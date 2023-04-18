package org.csbf.security.service.imp;

import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.csbf.security.constant.Role;
import org.csbf.security.event.OnRegistrationCompleteEvent;
import org.csbf.security.model.EmailVerificationToken;
import org.csbf.security.model.User;
import org.csbf.security.payload.AuthenticationRequest;
import org.csbf.security.payload.AuthenticationResponse;
import org.csbf.security.payload.RegisterRequest;
import org.csbf.security.repository.EmailVerificationTokenRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.AuthenticationService;
import org.csbf.security.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Array;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp implements AuthenticationService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    private EmailVerificationTokenRepository verificationTokenRepo;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent())
                return AuthenticationResponse.builder()
                        .success(false)
                        .message("user already exist")
                        .token(null)
                        .build();

//        String appUrl = servletRequest.getContextPath();
//        String roles = Role.USER.name()+"_"+Role.ADMIN.name();
        String roles = Role.USER.name();

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles).accountEnabled(false)
                .build();
//        User registeredUser = userRepo.save(user);
//        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registeredUser,
//                servletRequest.getLocale(), appUrl));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .success(true)
                .message("user created")
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

//        var user = userRepo.findByEmail(request.getEmail())
//                .orElseThrow();
        if (!userRepo.findByEmail(request.getEmail()).isPresent())
            return AuthenticationResponse.builder()
                    .success(false)
                    .message("user does not exist")
                    .token(null)
                    .user(null)
                    .build();

        var user = userRepo.findByEmail(request.getEmail())
                .orElseThrow();

        if (!user.isEnabled()) {
            return AuthenticationResponse.builder()
                    .success(false)
                    .message("account not enabled")
                    .token(null)
                    .user(user)
                    .build();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .success(true)
                .message("authenticated")
                .token(jwtToken)
                .user(user)
                .build();
    }

    @Override
    public EmailVerificationToken getEmailVerificationToken(String VerificationToken) {
        return verificationTokenRepo.findByToken(VerificationToken);
    }

    @Override
    public User getUser(String verificationToken) {
        return null;
    }

//    @Override
//    public void saveRegisteredUser(User user) {
//        repository.save(user);
//    }

    @Override
    public void createEmailVerificationToken(User user, String token) {
        EmailVerificationToken myToken = new EmailVerificationToken(user);
        verificationTokenRepo.save(myToken);
    }

    public Map confirmRegistration(HttpServletRequest servletRequest, String token) {

        Locale locale = servletRequest.getLocale();
        String message = null;
        Map<String, Object> map = new HashMap<String, Object>( );
        EmailVerificationToken verificationToken = getEmailVerificationToken(token);
        if (verificationToken == null) {
            message = "invalid token";
            map.put("success", false);
            map.put("token", null);
            map.put("success", message);
            return map;
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            message = "expired token";
            map.put("success", false);
            map.put("token", null);
            map.put("success", message);
            return map;
        }

        user.setAccountEnabled(true);
        userRepo.save(user);
        message = "account verified";
        map.put("success", true);
        map.put("token", verificationToken);
        map.put("success", message);
        return map;
    }
}
