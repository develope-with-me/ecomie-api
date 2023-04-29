package org.csbf.security.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface EmailService {
    @Async
    void sendEmailVerificationToken(String email) throws UnsupportedEncodingException;
    @Async
    void sendEmail(String recipient, String subject, String body);
//    Map sendEmail(String email);
//@Async
//    boolean resendEmail(String email);
}
