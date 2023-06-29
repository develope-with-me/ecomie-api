package org.csbf.security.event;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@AllArgsConstructor
public class OnSendMailEventListener implements ApplicationListener<OnSendMailEvent> {
    @Override
    public void onApplicationEvent(OnSendMailEvent event) {

    }
/*    private void completeRegistration(MailingEvent event) {
        HelperDto.MailingEventObject eventObject = event.getEventObject();
        Event actualEvent = event.getEvent();
        String otpCode = eventObject.otp();
        String recipientAddress = eventObject.email();
        String recipientName = eventObject.name();
        String subject = "";
        String htmlText = "";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, password);
                    }
                });

        try {

            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(email));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientAddress));

            // This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");

            // first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            if (actualEvent.equals(Event.ACTIVATION)) {
                subject = "Please verify your email address";
                htmlText = "<img src=\"cid:image\"><H2>" +
                        "Hello " + recipientName + " ,</br><H2>Thank you for registering on the Camsol payment gateway."
                        +
                        "\nPlease confirm your email address with the following code: " +
                        "</H2><H1>" + otpCode + "</H1></br>";
            } else if (actualEvent.equals(Event.FORGOT_PASSWORD)) {
                subject = "Reset password request";
                htmlText = "<img src=\"cid:image\"><H2>" +
                        "Hello " + recipientName + ",</br><H2>There has been a request to reset your password on the" +
                        " Camsol payment gateway.\n" +
                        "Please use the following code to reset your password: " +
                        "</H2><H1>" + otpCode + "</H1></br></br>" +
                        "<b>Please ignore this mail if you did not request a password reset.</b>";
            }

            // Set Subject: header field
            message.setSubject(subject);

            messageBodyPart.setContent(htmlText, "text/html");
            // add it
            multipart.addBodyPart(messageBodyPart);

            // put everything together
            message.setContent(multipart);
            // Send message
            Transport.send(message);

            log.info("Sent message successfully....");

        } catch (MessagingException e) {
            throw new FailedSendEmailException("Failed to send email");
        }
    }*/
}
