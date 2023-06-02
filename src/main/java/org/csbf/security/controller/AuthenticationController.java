package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.AuthenticationService;
import org.csbf.security.service.EmailService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final EmailService emailService;

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Create account", tags = {"Authentication"})
    public ResponseEntity<HelperDto.AuthenticationResponse> register(@RequestBody HelperDto.RegisterRequest request, HttpServletRequest servletRequest) {

        HelperDto.AuthenticationResponse response = service.register(request);
        var user = response.user();
        if (response.success()) {
            emailService.sendEmailVerificationToken(servletRequest.getHeader("host"), request.email());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate", description = "Authenticate user using email and password", tags = {"Authentication"})
    public ResponseEntity<HelperDto.AuthenticationResponse> authenticate(@RequestBody HelperDto.AuthenticationRequest request, HttpServletRequest servletRequest) {
        String appUrl = servletRequest.getContextPath();

        HelperDto.AuthenticationResponse response = service.authenticate(request);
        var user = response.user();
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Confirm Account", description = "Confirm users email address", tags = {"Authentication"})
    public ResponseEntity<HelperDto.ConfirmEmailResponse> confirmUserAccount(@RequestParam("email") String email, @RequestParam("token") String token) {
        log.info("Email {}", email);
        return ResponseEntity.ok(service.confirmEmail(email, token));
    }

    @RequestMapping(value = "/resend-link", method = {RequestMethod.POST})
    @Operation(summary = "Resend Confirmation Link", description = "Sends confirmation link to the email sent in request body", tags = {"Authentication"})
    public ResponseEntity<ResponseMessage> resendUserEmailConfirmationLink(@RequestBody HelperDto.ResendVerificationEmailDTO emailDTO, HttpServletRequest servletRequest) {
        log.info("Email {}", emailDTO);

        emailService.sendEmailVerificationToken(servletRequest.getHeader("host"), emailDTO.email());
        return ResponseEntity.ok(new ResponseMessage.SuccessResponseMessage("email resent"));
    }
}
