package org.csbf.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.dto.ResendVerificationEmailDTO;
import org.csbf.security.event.OnRegistrationCompleteEvent;
import org.csbf.security.model.EmailVerificationToken;
import org.csbf.security.payload.AuthenticationRequest;
import org.csbf.security.payload.AuthenticationResponse;
import org.csbf.security.payload.RegisterRequest;
import org.csbf.security.repository.EmailVerificationTokenRepository;
import org.csbf.security.service.AuthenticationService;
import org.csbf.security.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    EmailVerificationTokenRepository verificationTokenRepo;

    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, HttpServletRequest servletRequest) throws UnsupportedEncodingException {

        AuthenticationResponse response = service.register(request);
        var user = response.getUser();
        response.setUser(null);
        if( response.isSuccess()) {
                        emailService.sendEmailVerificationToken(user.getEmail());
//            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(response.getUser(),
//                    servletRequest.getLocale(), appUrl));
        }

        return ResponseEntity.ok(response);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest servletRequest) {
        String appUrl = servletRequest.getContextPath();

        AuthenticationResponse response = service.authenticate(request);
        var user = response.getUser();
//        response.setUser(null);
//        if( user==null)
//            return ResponseEntity.ok(response);
//        if( !user.isEnabled())
//            String token = UUID.randomUUID().toString();
//            EmailVerificationToken emailVerificationToken = new EmailVerificationToken(user);
//            emailVerificationToken.setToken(token);
//
//
//            verificationTokenRepo.save(emailVerificationToken);
//
//            SimpleMailMessage mailMessage = new SimpleMailMessage();
//            mailMessage.setTo(user.getEmail());
//            mailMessage.setSubject("Complete Registration!");
//            mailMessage.setText("To confirm your account, please click here : "
//                    + "http://localhost:8080/confirm-account?token=" + emailVerificationToken.getToken());
//            emailService.sendEmail(mailMessage);

//            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(response.getUser(),
//                    servletRequest.getLocale(), appUrl));

        return ResponseEntity.ok(response);
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map> confirmUserAccount(@RequestParam("email") String email, @RequestParam("token") String token) throws UnsupportedEncodingException {
log.info("Email {}", email);
        return ResponseEntity.ok(service.confirmEmail(email, token));
    }

    @RequestMapping(value="/resend-link", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map> resendUserEmailConfirmationLink(@RequestBody ResendVerificationEmailDTO emailDTO) throws UnsupportedEncodingException {
        log.info("Email {}", emailDTO);

        emailService.sendEmailVerificationToken(emailDTO.getEmail());
        Map<String, Object> response = new HashMap<>();
//        String message = success ? "email sent" : "email not sent";
        response.put("success", true);
        response.put("message", "email resent");
//
//        return response;
//        return ResponseEntity.ok(emailService.sendEmail(email));
        return ResponseEntity.ok(response);
    }

//    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
//    public ResponseEntity<?> confirmUserAccount(@RequestParam("token")String confirmationToken) {
//        return userService.confirmEmail(confirmationToken);
//    }
}
