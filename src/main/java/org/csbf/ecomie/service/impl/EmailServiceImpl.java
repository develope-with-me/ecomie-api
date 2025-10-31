package org.csbf.ecomie.service.impl;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.csbf.ecomie.constant.TokenType;
import org.csbf.ecomie.entity.UserTokenEntity;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.entity.UserEntity;
import org.csbf.ecomie.repository.UserRepository;
import org.csbf.ecomie.repository.UserTokenRepository;
import org.csbf.ecomie.service.AuthenticationService;
import org.csbf.ecomie.service.EmailService;
import org.csbf.ecomie.service.UserTokenService;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepo;
    private final UserTokenRepository tokenRepo;
    private final AuthenticationService authService;
    private final UserTokenService tokenService;
    private final Environment env;
    private final HttpServletRequest request;
//    private final int EMAIL_VERIFICATION_TOKEN_DURATION_IN_MINUTES = Integer.parseInt(Objects.requireNonNull(env.getProperty("email-verification.token.duration")));
//    private final int PASSWORD_RESET_TOKEN_DURATION_IN_MINUTES = Integer.parseInt(Objects.requireNonNull(env.getProperty("password-reset.token.duration")));
    private static final String REGISTRATION_TEMPLATE = "src/main/resources/templates/registration-template.hbs";
    private static final String PASSWORD_RESET_TEMPLATE = "src/main/resources/templates/password-reset-template.hbs";

    @Async
    @Override
    public void sendEmailVerificationToken(String requestHost, String email) {
        String token = tokenService.createToken();
        boolean success = false;
        if (userRepo.findByEmail(email).isPresent()) {
            UserEntity userEntity = userRepo.findByEmail(email).get();
            var tokenEntity = UserTokenEntity.builder()
                    .user(userEntity)
                    .token(token)
                    .type(TokenType.EMAIL_VERIFICATION)
                    .build();
//            tokenEntity.setExpiryDate(EMAIL_VERIFICATION_TOKEN_DURATION_IN_MINUTES);
            tokenEntity = tokenRepo.save(tokenEntity);

            String encodedEmail;
            encodedEmail = URLEncoder.encode(userEntity.getEmail(), StandardCharsets.UTF_8);

            String recipient = userEntity.getEmail();
            String subject = "Complete Registration!";

            // Validate email and token
            if (tokenEntity.getToken() == null) {
                throw Problems.INVALID_PARAMETER_ERROR.appendDetail("Invalid email or verification token").toException();
            }

            // Construct the confirmation link
            String confirmationLink = String.format("%s://%s/api/v1/auth/confirm-account?email=%s&token=%s",
                    request.getScheme(), requestHost, encodedEmail, tokenEntity.getToken());
            log.info("Confirmation Link: {}", confirmationLink);
            String logoUrl = env.getProperty("app.logo.url");
            log.info("Logo URL: {}", logoUrl);

            // Create the data map
            Map<String, Object> data = new HashMap<>();
            data.put("firstName", userEntity.getFirstName());
            data.put("confirmationLink", confirmationLink);
            data.put("logoUrl", logoUrl);

//            var templatePath = env.getProperty("app.template.path");

            processHandleBar(data, recipient, subject, REGISTRATION_TEMPLATE);
        }
    }


    @Async
    @Override
    public void sendEmail(String recipient, String subject, String body, String... from ) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        if(!ArrayUtils.isEmpty(from)) {
            mimeMessage.setFrom(from[0]);
        }
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true); // Set to true to indicate that the content is HTML
        try {
            log.info("EmailService.sendEmail - Sending email");
            javaMailSender.send(mimeMessage);
            log.info("EmailService.sendEmail - Successfully sent email");
        }catch(MailException e) {
            throw Problems.OBJECT_VALIDATION_ERROR.appendDetail(e.getMessage()).toException();
        }
    }

    @Async
    @Override
    public void sendCustomEmail(String requestHost, String from, String to, String purpose) throws MessagingException {
        String encodedToEmail;
        String encodedFromEmail;

        encodedToEmail = URLEncoder.encode(to, StandardCharsets.UTF_8);
        encodedFromEmail = URLEncoder.encode(from, StandardCharsets.UTF_8);
        String subject = "@ECOMIE - Request To Be " + purpose;
        String body = "I will like to become a/an " + purpose + ": \n\n If you approve of this userEntity, click on the link to approve his/her request " + requestHost +  "/api/v1/secure/admin/update-userEntity-role?email="+encodedFromEmail+"&role="+purpose;
        sendEmail(to, subject, body, from);

    }

    @Async
    @Override
    public void requestPasswordReset(String requestHost, String email) {
        String token = tokenService.createToken();
        boolean success = false;
        if (userRepo.findByEmail(email).isPresent()) {
            UserEntity userEntity = userRepo.findByEmail(email).get();
            var tokenEntity = UserTokenEntity.builder()
                    .user(userEntity)
                    .token(token)
                    .type(TokenType.EMAIL_VERIFICATION)
                    .build();
//            tokenEntity.setExpiryDate(EMAIL_VERIFICATION_TOKEN_DURATION_IN_MINUTES);
            tokenEntity = tokenRepo.save(tokenEntity);

            String encodedEmail;
            encodedEmail = URLEncoder.encode(userEntity.getEmail(), StandardCharsets.UTF_8);

            String recipient = userEntity.getEmail();
            String subject = "Reset Password!";

            // Validate email and token
            if (tokenEntity.getToken() == null) {
                throw Problems.INVALID_PARAMETER_ERROR.appendDetail("Invalid Reset Token").toException();
            }

            // Construct the Reset link
            String resetLink = String.format("%s://%s/api/v1/auth/reset-password?email=%s&token=%s",
                    request.getScheme(), requestHost, encodedEmail, tokenEntity.getToken());
            log.info("Reset Link: {}", resetLink);
            String logoUrl = env.getProperty("app.logo.url");
            log.info("Logo URL: {}", logoUrl);

            // Create the data map
            Map<String, Object> data = new HashMap<>();
            data.put("firstName", userEntity.getFirstName());
            data.put("resetLink", resetLink);
            data.put("logoUrl", logoUrl);

            processHandleBar(data, recipient, subject, PASSWORD_RESET_TEMPLATE);
        }
    }

    private void processHandleBar(Map<String, Object> data, String recipient, String subject, String templatePath) {
        // Load and compile the Handlebars template
        String templateContent;
        try {
            log.info("EmailServiceImpl.processHandleBar - Reading Template");
            templateContent = Files.readString(Paths.get("src/main/resources/templates/password-reset-template.hbs"));
            templateContent = Files.readString(Paths.get(templatePath));
            log.info("EmailServiceImpl.processHandleBar - Successfully Read Template");


            Handlebars handlebars = new Handlebars();
            Template template = handlebars.compileInline(templateContent);

            // Create the email body by applying the data
            String body = template.apply(data);

            // Log the email sending process
            log.info("Sending email template to: {}", recipient);
            sendEmail(recipient, subject, body);

        } catch (IOException | MessagingException e) {
            throw Problems.OBJECT_VALIDATION_ERROR.appendDetail("Could not load email template").toException();
        }
    }

}
