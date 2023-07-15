package org.csbf.security.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.EmailService;
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
    public void onApplicationEvent(OnRoleChangeRequestEvent event) { this.sendEmail(event);

    }
    private void sendEmail(OnRoleChangeRequestEvent event) {
        service.sendCustomEmail(event.getRequestHeaderHost(), event.getEmail(), env.getProperty("SUPER_ADMIN_EMAIL"), event.getRole());
}
}
