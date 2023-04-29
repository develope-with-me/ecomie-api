package org.csbf.security.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.csbf.security.model.EmailVerificationToken;
import org.csbf.security.model.User;
import org.csbf.security.repository.EmailVerificationTokenRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.AuthenticationService;
import org.csbf.security.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class EmailServiceImp implements EmailService {
    private JavaMailSender javaMailSender;

    @Autowired
    UserRepository userRepo;
    @Autowired
    AuthenticationService authService;

    @Autowired
    EmailVerificationTokenRepository verificationTokenRepo;

    @Autowired
    public EmailServiceImp(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

//    @Async
//    @Override
//    public boolean sendEmail(SimpleMailMessage mailMessage) {
//        javaMailSender.send(mailMessage);
//    }

    @Async
    @Override
//    public Map<String, Object> sendEmail(String email) {
    public void sendEmailVerificationToken(String email) throws UnsupportedEncodingException {
        String token = authService.createEmailVerificationToken();
        boolean success = false;
        System.out.println(email);
        System.out.println(userRepo.findByEmail(email).get());
        System.out.println(userRepo.findByEmail(email).isPresent());
        System.out.println(userRepo.findByEmail(email));
        if (userRepo.findByEmail(email).isPresent()) {
            User user = userRepo.findByEmail(email).get();
            user.setEmailVerificationToken(token);
            userRepo.save(user);


            /*
            EmailVerificationToken emailVerificationToken = new EmailVerificationToken(user);
            emailVerificationToken.setToken(token);
            emailVerificationToken.setUser(user);



            verificationTokenRepo.save(emailVerificationToken);
            */

            String encodedEmail = URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8.toString());

            String recipient = user.getEmail();
            String subject = "Complete Registration!";
            String body = "To confirm your account, please click here : " + "http://localhost:8080/api/v1/auth/confirm-account?email=" + encodedEmail +"&token=" + user.getEmailVerificationToken();

            sendEmail(recipient, subject, body);

//            success = true;
        }

//        Map<String, Object> response = new HashMap<>();
//        String message = success ? "email sent" : "email not sent";
//        response.put("success", success);
//        response.put("message", message);
//
//        return response;
    }


    @Async
    @Override
    public void sendEmail(String recipient, String subject, String body ) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipient);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        javaMailSender.send(mailMessage);
    }

}
