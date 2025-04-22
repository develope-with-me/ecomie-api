package org.csbf.security.service.imp;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.csbf.security.exceptions.EmailTemplateException;
import org.csbf.security.exceptions.FailedSendEmailException;
import org.csbf.security.exceptions.ResourceNotFoundException;
import org.csbf.security.model.User;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.AuthenticationService;
import org.csbf.security.service.EmailService;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepo;
    private final AuthenticationService authService;
    private final Environment env;
    private final HttpServletRequest request;

    @Async
    @Override
    public void sendEmailVerificationToken(String requestHost, String email) {
        String token = authService.createEmailVerificationToken();
        boolean success = false;
        if (userRepo.findByEmail(email).isPresent()) {
            User user = userRepo.findByEmail(email).get();
            user.setEmailVerificationToken(token);
            userRepo.save(user);

            String encodedEmail;
            encodedEmail = URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8);

            String recipient = user.getEmail();
            String subject = "Complete Registration!";

            // Validate email and token
            if (recipient == null || recipient.isEmpty() || user.getEmailVerificationToken() == null) {
                throw new ResourceNotFoundException("Invalid email or verification token");
            }

            // Construct the confirmation link
            String confirmationLink = String.format("%s://%s/api/v1/auth/confirm-account?email=%s&token=%s",
                    request.getScheme(), requestHost, encodedEmail, user.getEmailVerificationToken());
            log.info("Confirmation Link: {}", confirmationLink);
            String logoUrl = env.getProperty("app.logo.url");
            log.info("Logo URL: {}", logoUrl);

            // Create the data map
            Map<String, Object> data = new HashMap<>();
            data.put("firstName", user.getFirstname());
            data.put("confirmationLink", confirmationLink);
            data.put("logoUrl", logoUrl);



            // Load and compile the Handlebars template
            String templateContent;
            try {
                log.info("EmailService.sendEmailVerificationToken - Reading Template");
                templateContent = Files.readString(Paths.get("src/main/resources/templates/registration-template.hbs"));
                log.info("EmailService.sendEmailVerificationToken - Successfully Read Template");


                Handlebars handlebars = new Handlebars();
                Template template = handlebars.compileInline(templateContent);

                // Create the email body by applying the data
                String body = template.apply(data);

                // Log the email sending process
                log.info("Sending confirmation email to: {}", recipient);
                sendEmail(recipient, subject, body);

            } catch (IOException | MessagingException e) {
                throw new EmailTemplateException("Could not load email template");
            }
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
        javaMailSender.send(mimeMessage);

        try {
            log.info("EmailService.sendEmail - Sending email");
            javaMailSender.send(mimeMessage);
            log.info("EmailService.sendEmail - Successfully sent email");
        }catch(MailException e) {
            throw new FailedSendEmailException(e.getMessage());
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
        String body = "I will like to become a/an " + purpose + ": \n\n If you approve of this user, click on the link to approve his/her request " + requestHost +  "/api/v1/secure/admin/update-user-role?email="+encodedFromEmail+"&role="+purpose;
        sendEmail(to, subject, body, from);

    }

}
