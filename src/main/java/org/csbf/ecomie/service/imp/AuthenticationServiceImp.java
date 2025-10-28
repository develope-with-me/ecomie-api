package org.csbf.ecomie.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.exceptions.*;
import org.csbf.ecomie.entity.EmailVerificationTokenEntity;
import org.csbf.ecomie.entity.UserEntity;
import org.csbf.ecomie.mapper.UserMapper;
import org.csbf.ecomie.repository.EmailVerificationTokenRepository;
import org.csbf.ecomie.repository.UserRepository;
import org.csbf.ecomie.service.AuthenticationService;
import org.csbf.ecomie.service.JwtService;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp implements AuthenticationService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationTokenRepository verificationTokenRepo;
    private final UserMapper userMapper;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        String msg = "user already exist";
        if (userRepo.findByEmail(request.email()).isPresent())
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("challengeEntity.name", "Email (%s) already in use".formatted(request.email())).toException();

//        String appUrl = servletRequest.getContextPath();
//        String roles = Role.USER.name()+"-"+Role.ADMIN.name();
//        String roles = Role.USER.name();

        var user = UserEntity.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .accountBlocked(false)
                .accountEnabled(false)
                .accountSoftDeleted(false)
                .build();
        UserEntity registeredUserEntity = userRepo.save(user);

        var jwtToken = jwtService.generateToken(user);
        msg = "user created";
        return getAuthenticationResponse(true, msg, jwtToken);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String message =  "user does not exist";
        var user = userRepo.findByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException.EmailNotFoundException(request.email()));

//        var user = userRepo.findByEmail(request.email()).get();

        if (!user.isEnabled()) {
            message = "account not enabled";
            return getAuthenticationResponse(false, message, null);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch(AuthenticationException e) {
            throw Problems.UNAUTHORIZED.withDetail("Email and password do not match").toException();
        }

        var jwtToken = jwtService.generateToken(user);
//        var jwtToken = jwtService.gen(user);
        message = "authenticated";
        return getAuthenticationResponse(true, message, jwtToken, user);
    }

    @Override
    public EmailVerificationTokenEntity getEmailVerificationToken(String VerificationToken) {
        return verificationTokenRepo.findByToken(VerificationToken);
    }


    @Override
    public String createEmailVerificationToken() {
        return UUID.randomUUID().toString();
    }
    @Override
    public ConfirmEmailResponse confirmEmail(String email, String token) {
        String decodedEmail;
        try {
            decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw Problems.INCONSISTENT_DATA_ERROR.withDetail("Could not encode email").toException();
        }

        var obj = userRepo.findByEmail(email);
        log.info("decodedEmail {}", email);
        log.info("UserEntity {}", obj.get());

        UserEntity userEntity = userRepo.findByEmailAndEmailVerificationToken(email, token).isPresent() ? userRepo.findByEmailVerificationToken(token).get() : null;
        if (userEntity != null) {
            userEntity.setAccountEnabled(true);
            userRepo.save(userEntity);
            return new ConfirmEmailResponse(userEntity.getEmailVerificationToken(), new ResponseMessage.SuccessResponseMessage("account verified"));
        }

        return new ConfirmEmailResponse(null, new ResponseMessage.ExceptionResponseMessage("user and token do not match"));
    }

    private AuthenticationResponse getAuthenticationResponse(boolean success, String message, String jwtToken, UserEntity... userEntities) {
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message(message)
                .success(success)
                .user(!ArrayUtils.isEmpty(userEntities) ? userMapper.asDomainObject(userEntities[0]).justMinimal() : null)
                .build();
    }
}
