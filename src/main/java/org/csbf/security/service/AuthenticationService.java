package org.csbf.security.service;

import lombok.RequiredArgsConstructor;
import org.csbf.security.constant.Role;
import org.csbf.security.model.User;
import org.csbf.security.payload.AuthenticationRequest;
import org.csbf.security.payload.AuthenticationResponse;
import org.csbf.security.payload.RegisterRequest;
import org.csbf.security.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent())
                return AuthenticationResponse.builder()
                        .success(false)
                        .message("user already exist")
                        .token(null)
                        .build();

//        String roles = Role.USER.name()+"_"+Role.ADMIN.name();
        String roles = Role.USER.name();

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();
        userRepo.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .success(true)
                .message("user created")
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

//        var user = userRepo.findByEmail(request.getEmail())
//                .orElseThrow();
        if (!userRepo.findByEmail(request.getEmail()).isPresent())
            return AuthenticationResponse.builder()
                    .success(false)
                    .message("user does not exist")
                    .token(null)
                    .build();

        var user = userRepo.findByEmail(request.getEmail())
                .orElseThrow();

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
                .build();
    }
}
