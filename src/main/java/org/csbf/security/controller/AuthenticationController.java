package org.csbf.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.csbf.security.event.OnRegistrationCompleteEvent;
import org.csbf.security.payload.AuthenticationRequest;
import org.csbf.security.payload.AuthenticationResponse;
import org.csbf.security.payload.RegisterRequest;
import org.csbf.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        String appUrl = servletRequest.getContextPath();

        AuthenticationResponse response = service.register(request);
        if( response.isSuccess())
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(response.getUser(),
                servletRequest.getLocale(), appUrl));

        return ResponseEntity.ok(response);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest servletRequest) {
        String appUrl = servletRequest.getContextPath();

        AuthenticationResponse response = service.authenticate(request);
        var user = response.getUser();
        response.setUser(null);
        if( user==null)
            return ResponseEntity.ok(response);
        if( !user.isEnabled())
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(response.getUser(),
                    servletRequest.getLocale(), appUrl));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<Map> confirmRegistration(HttpServletRequest servletRequest, @RequestParam("token") String token) {

        Locale locale = servletRequest.getLocale();

        return ResponseEntity.ok(service.confirmRegistration(servletRequest, token));
//        "redirect:/badUser.html?lang=" + locale.getLanguage();
    }
}
