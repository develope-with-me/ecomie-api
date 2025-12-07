package org.csbf.ecomie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.exceptions.*;
import org.csbf.ecomie.entity.UserTokenEntity;
import org.csbf.ecomie.entity.UserEntity;
import org.csbf.ecomie.mapper.UserMapper;
import org.csbf.ecomie.repository.UserTokenRepository;
import org.csbf.ecomie.repository.UserRepository;
import org.csbf.ecomie.service.AuthenticationService;
import org.csbf.ecomie.service.JwtService;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepo;
    private final UserTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserTokenRepository verificationTokenRepo;
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
    public ConfirmEmailResponse confirmEmail(String email, String token) {
        String decodedEmail;
     decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);

     var obj = userRepo.findByEmail(email);
        log.info("decodedEmail {}", email);
        log.info("UserEntity {}", obj.get());

        UserTokenEntity tokenEntity = tokenRepo.findByTokenAndUser_Email(token, email).orElseThrow(() -> Problems.NOT_FOUND.appendDetail("Token not found").toException());
        if (!tokenEntity.isExpired() || !tokenEntity.getIsValid()) {
            throw Problems.INCONSISTENT_DATA_ERROR.withDetail("Invalid or expired token").toException();
        }
        var userEntity = tokenEntity.getUser();

        if (userEntity != null) {
            userEntity.setAccountEnabled(true);
            userRepo.save(userEntity);
            tokenEntity.setIsValid(false);
            tokenRepo.save(tokenEntity);
            return new ConfirmEmailResponse(tokenEntity.getToken(), new ResponseMessage.SuccessResponseMessage("account verified"));
        }

        return new ConfirmEmailResponse(null, new ResponseMessage.ExceptionResponseMessage("user and token do not match"));
    }

    @Override
    public ConfirmEmailResponse resetPassword(PasswordDTO passwordDTO, String email, String token) {
        String decodedEmail;
        decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);

        var obj = userRepo.findByEmail(email);
        log.info("decodedEmail {}", email);
        log.info("UserEntity {}", obj.get());

        UserTokenEntity tokenEntity = tokenRepo.findByTokenAndUser_Email(token, email).orElseThrow(() -> Problems.NOT_FOUND.appendDetail("Token not found").toException());
        if (!tokenEntity.isExpired() || !tokenEntity.getIsValid()) {
            throw Problems.INCONSISTENT_DATA_ERROR.withDetail("Invalid or expired token").toException();
        }
        if(!passwordDTO.confirmPassword().equals(passwordDTO.password())) {
            throw Problems.OBJECT_VALIDATION_ERROR.withDetail("Invalid or expired token").toException();
        }
        var userEntity = tokenEntity.getUser();
        if (userEntity != null) {
            userEntity.setPassword(passwordEncoder.encode(passwordDTO.password()));
            userRepo.save(userEntity);
            tokenEntity.setIsValid(false);
            tokenRepo.save(tokenEntity);
            return new ConfirmEmailResponse(tokenEntity.getToken(), new ResponseMessage.SuccessResponseMessage("Password reset successful, please login with your new password"));
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
