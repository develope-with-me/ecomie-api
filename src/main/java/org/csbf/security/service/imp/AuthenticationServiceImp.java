package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.constant.Role;
import org.csbf.security.utils.UserDTO;
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
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
        String msg = "user already exist";
        if (userRepo.findByEmail(request.getEmail()).isPresent())
            return getAuthenticationResponse(false, msg, null, null);

//        String appUrl = servletRequest.getContextPath();
//        String roles = Role.USER.name()+"-"+Role.ADMIN.name();
        String roles = Role.USER.name();

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .accountBlocked(false)
                .accountEnabled(false)
                .build();
        User registeredUser = userRepo.save(user);

//        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registeredUser,
//                servletRequest.getLocale(), appUrl));
        var jwtToken = jwtService.generateToken(user);
        msg = "user created";
        return getAuthenticationResponse(true, msg, jwtToken, registeredUser);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String message =  "user does not exist";
        if (!userRepo.findByEmail(request.getEmail()).isPresent())
            return getAuthenticationResponse(false, message, null, null);

        var user = userRepo.findByEmail(request.getEmail())
                .orElseThrow();

        if (!user.isEnabled()) {
            message = "account not enabled";
            return getAuthenticationResponse(false, message, null, null);
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        message = "authenticated";
        return getAuthenticationResponse(true, message, jwtToken, user);
    }

    @Override
    public EmailVerificationToken getEmailVerificationToken(String VerificationToken) {
        return verificationTokenRepo.findByToken(VerificationToken);
    }

//    @Override
//    public User getUser(String verificationToken) {
//        return null;
//    }

//    @Override
//    public void saveRegisteredUser(User user) {
//        repository.save(user);
//    }

    @Override
    public String createEmailVerificationToken() {
        return UUID.randomUUID().toString();
    }
    @Override
    public Map confirmEmail(String email, String token) throws UnsupportedEncodingException {
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8.toString());
        String message = null;
        Map<String, Object> map = new HashMap<String, Object>( );
        var obj = userRepo.findByEmail(email);
        var obj1 = userRepo.findByEmail(decodedEmail);
        log.info("decodedEmail {}", email);
        log.info("User {}", obj.get());

//        User user = userRepo.findByEmailAndEmailVerificationToken(decodedEmail, token).isPresent() ? userRepo.findByEmailVerificationToken(token).get() : null;
        User user = userRepo.findByEmailAndEmailVerificationToken(email, token).isPresent() ? userRepo.findByEmailVerificationToken(token).get() : null;
        if (user == null) {
            message = "user and token do not match";
            map.put("success", false);
            map.put("token", null);
            map.put("message", message);
            return map;
        }

        /*String verificationToken = user.getEmailVerificationToken();
        if (verificationToken == null) {
            message = "invalid token";
            map.put("success", false);
            map.put("token", null);
            map.put("message", message);
            return map;
        }*/

        /*User user = verificationToken.getUser();
        if (verificationToken.getExpiryDate() != null) {
            Calendar cal = Calendar.getInstance();
            if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
                message = "expired token";
                map.put("success", false);
                map.put("token", null);
                map.put("message", message);
                return map;
            }
        }*/
        user.setAccountEnabled(true);
        userRepo.save(user);
        message = "account verified";
        map.put("success", true);
        map.put("token", user.getEmailVerificationToken());
        map.put("message", message);
        return map;
    }

    public UserDTO getUserDTO(User user) {
       return user == null ? null : UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .accountEnabled(user.isAccountEnabled())
                .accountBlocked(user.isAccountBlocked())
                .build();
    }
    private AuthenticationResponse getAuthenticationResponse(boolean success, String message,  String jwtToken, User user) {
        return AuthenticationResponse.builder()
                .success(success)
                .message(message)
                .token(jwtToken)
                .user(getUserDTO(user))
                .build();
    }
}
