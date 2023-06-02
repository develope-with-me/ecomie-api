package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.constant.Role;
import org.csbf.security.exceptions.BaseException;
import org.csbf.security.model.EmailVerificationToken;
import org.csbf.security.model.User;
import org.csbf.security.repository.EmailVerificationTokenRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.AuthenticationService;
import org.csbf.security.service.JwtService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp implements AuthenticationService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationTokenRepository verificationTokenRepo;

    @Override
    public HelperDto.AuthenticationResponse register(HelperDto.RegisterRequest request) {
        String msg = "user already exist";
        if (userRepo.findByEmail(request.email()).isPresent())
            return getAuthenticationResponse(false, msg, null, null);

//        String appUrl = servletRequest.getContextPath();
//        String roles = Role.USER.name()+"-"+Role.ADMIN.name();
        String roles = Role.USER.name();

        var user = User.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(roles)
                .accountBlocked(false)
                .accountEnabled(false)
                .build();
        User registeredUser = userRepo.save(user);

        var jwtToken = jwtService.generateToken(user);
        msg = "user created";
        return getAuthenticationResponse(true, msg, jwtToken);
    }

    @Override
    public HelperDto.AuthenticationResponse authenticate(HelperDto.AuthenticationRequest request) {
        String message =  "user does not exist";
        if (!userRepo.findByEmail(request.email()).isPresent())
            return getAuthenticationResponse(false, message, null);

        var user = userRepo.findByEmail(request.email()).get();

        if (!user.isEnabled()) {
            message = "account not enabled";
            return getAuthenticationResponse(false, message, null);
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
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


    @Override
    public String createEmailVerificationToken() {
        return UUID.randomUUID().toString();
    }
    @Override
    public HelperDto.ConfirmEmailResponse confirmEmail(String email, String token) {
        String decodedEmail;
        try {
            decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new BaseException("Could not encode email");
        }

        var obj = userRepo.findByEmail(email);
        log.info("decodedEmail {}", email);
        log.info("User {}", obj.get());

        User user = userRepo.findByEmailAndEmailVerificationToken(email, token).isPresent() ? userRepo.findByEmailVerificationToken(token).get() : null;
        if (user != null) {
            user.setAccountEnabled(true);
            userRepo.save(user);
            return new HelperDto.ConfirmEmailResponse(user.getEmailVerificationToken(), new ResponseMessage.SuccessResponseMessage("account verified"));
        }

        return new HelperDto.ConfirmEmailResponse(null, new ResponseMessage.ExceptionResponseMessage("user and token do not match"));
    }

    public HelperDto.UserDto getUserDTO(User user) {
       return user == null ? null : HelperDto.UserDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .accountEnabled(user.isAccountEnabled())
                .accountBlocked(user.isAccountBlocked())
                .build();
    }
    private HelperDto.AuthenticationResponse getAuthenticationResponse(boolean success, String message, String jwtToken, User... users) {
        return HelperDto.AuthenticationResponse.builder()
                .token(jwtToken)
                .message(message)
                .success(success)
                .user(getUserDTO(users.length > 0 ? users[0] : null))
                .build();
    }
}
