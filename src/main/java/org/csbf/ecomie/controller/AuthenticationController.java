package org.csbf.ecomie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.ecomie.event.OnRegistrationCompleteEvent;
import org.csbf.ecomie.service.AuthenticationService;
import org.csbf.ecomie.service.EmailService;
import org.csbf.ecomie.utils.commons.ExtendedEmailValidator;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "AuthenticationController", description = "This controller contains endpoints for authentication")
public class AuthenticationController {
    private final AuthenticationService service;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final EmailService emailService;


    @PostMapping("/register")
    @Operation(summary = "Register", description = "Create account", tags = {"Unauthenticated"})
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        /**For Production */
        /*
        * if (EmailValidator.getInstance().isValid(request.email()))
        *    throw new BadRequestException("Invalid '" + request.email() + "' email address");
        * */

        AuthenticationResponse response = service.register(request);
        log.info("{}", response.toString());
        if (response.success()) {
            applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(servletRequest.getHeader("host"), request.email()));
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate", description = "Authenticate user using email and password", tags = {"Unauthenticated"})
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest servletRequest) {
        String appUrl = servletRequest.getContextPath();

        /**For Production */
        /*
         * if (EmailValidator.getInstance().isValid(request.email()))
         *    throw new BadRequestException("Invalid '" + request.email() + "' email address");
         * */

        AuthenticationResponse response = service.authenticate(request);
        var user = response.user();
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Confirm Account", description = "Confirm users email address", tags = {"Unauthenticated"})
    public ResponseEntity<ConfirmEmailResponse> confirmUserAccount(@ExtendedEmailValidator @RequestParam("email") String email, @NotBlank  @RequestParam("token") String token) {
        log.info("Email {}", email);
        return ResponseEntity.ok(service.confirmEmail(email, token));
    }

    @RequestMapping(value = "/resend-link", method = {RequestMethod.POST})
    @Operation(summary = "Resend Confirmation Link", description = "Sends confirmation link to the email sent in request body", tags = {"Unauthenticated"})
    public ResponseEntity<ResponseMessage> resendUserEmailConfirmationLink(@RequestBody EmailDTO emailDTO, HttpServletRequest servletRequest) {
        log.info("Email {}", emailDTO);
        /**For Production */
        /*
         * if (EmailValidator.getInstance().isValid(emailDTO.email()))
         *    throw new BadRequestException("Invalid '" + emailDTO.email() + "' email address");
         * */

        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(servletRequest.getHeader("host"), emailDTO.email()));

        return ResponseEntity.ok(new ResponseMessage.SuccessResponseMessage("email resent"));
    }

    @RequestMapping(value = "/reset-password/link", method = {RequestMethod.POST})
    @Operation(summary = "Password Reset Link", description = "Send password reset link to the email sent in request body", tags = {"Unauthenticated"})
    public ResponseEntity<ResponseMessage> requestPasswordReset(@RequestBody EmailDTO emailDTO, HttpServletRequest servletRequest) {
        log.info("Email {}", emailDTO);
        /**For Production */
        /*
         * if (EmailValidator.getInstance().isValid(emailDTO.email()))
         *    throw new BadRequestException("Invalid '" + emailDTO.email() + "' email address");
         * */

        emailService.requestPasswordReset(servletRequest.getHeader("host"), emailDTO.email());

        return ResponseEntity.ok(new ResponseMessage.SuccessResponseMessage("email sent"));
    }

    @RequestMapping(value = "/reset-password", method = {RequestMethod.POST})
    @Operation(summary = "Reset Password", description = "Reset password", tags = {"Unauthenticated"})
    public ResponseEntity<ConfirmEmailResponse> resetPassword(@RequestBody PasswordDTO passwordDTO, @ExtendedEmailValidator @RequestParam("email") String email, @NotBlank  @RequestParam("token") String token) {
        log.info("AuthenticationController.resetPassword {}", AuthenticationController.class.getSimpleName());
        /**For Production */
        /*
         * if (EmailValidator.getInstance().isValid(emailDTO.email()))
         *    throw new BadRequestException("Invalid '" + emailDTO.email() + "' email address");
         * */

        ;

        return ResponseEntity.ok(service.resetPassword(passwordDTO, email, token));
    }
}
