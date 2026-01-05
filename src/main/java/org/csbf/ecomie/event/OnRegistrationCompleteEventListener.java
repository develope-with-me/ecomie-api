package org.csbf.ecomie.event;

import lombok.RequiredArgsConstructor;

import org.csbf.ecomie.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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
        }
}
