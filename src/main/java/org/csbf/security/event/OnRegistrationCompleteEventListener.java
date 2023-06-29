package org.csbf.security.event;

import lombok.RequiredArgsConstructor;

import org.csbf.security.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
@RequiredArgsConstructor
public class OnRegistrationCompleteEventListener implements ApplicationListener<OnRegistrationCompleteEvent> {

        private final EmailService service;

        @Override
        public void onApplicationEvent(OnRegistrationCompleteEvent event) {
            this.confirmRegistration(event);
        }

        private void confirmRegistration(OnRegistrationCompleteEvent event) {

            service.sendEmailVerificationToken(event.getRequestHeaderHost(), event.getEmail());
//            User user = event.getUser();
//            String token = UUID.randomUUID().toString();
////            service.createEmailVerificationToken(user, token);
//
//            String recipientAddress = user.getEmail();
//            String subject = "Registration Confirmation";
//            String confirmationUrl = event.getAppUrl() + "/confirm-registration?token=" + token;
//            String message = messages.getMessage("message.regSucc", null, event.getLocale());
//
//            SimpleMailMessage email = new SimpleMailMessage();
//            email.setTo(recipientAddress);
//            email.setSubject(subject);
//            email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
//            mailSender.send(email);
        }
}
