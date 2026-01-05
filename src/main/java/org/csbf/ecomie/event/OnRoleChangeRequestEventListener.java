package org.csbf.ecomie.event;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.ecomie.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OnRoleChangeRequestEventListener implements ApplicationListener<OnRoleChangeRequestEvent> {
    private final EmailService service;
    private final Environment env;

    @Override
    public void onApplicationEvent(OnRoleChangeRequestEvent event) {
        try {
            this.sendEmail(event);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
    private void sendEmail(OnRoleChangeRequestEvent event) throws MessagingException {
        service.sendCustomEmail(event.getRequestHeaderHost(), event.getEmail(), env.getProperty("SUPER_ADMIN_EMAIL"), event.getRole());
}
}
