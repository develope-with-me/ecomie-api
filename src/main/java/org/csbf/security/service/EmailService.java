package org.csbf.security.service;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendEmailVerificationToken(String requestHost, String email);
    @Async
    void sendEmail(String recipient, String subject, String body, String... from);
    @Async
    void sendCustomEmail(String requestHeaderHost, String from, String to, String purpose);
}
