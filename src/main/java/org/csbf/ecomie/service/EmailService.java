package org.csbf.ecomie.service;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface EmailService {
    @Async
    void sendEmailVerificationToken(String requestHost, String email);
    @Async
    void sendEmail(String recipient, String subject, String body, String... from) throws MessagingException;
    @Async
    void sendCustomEmail(String requestHeaderHost, String from, String to, String purpose) throws MessagingException;
}
