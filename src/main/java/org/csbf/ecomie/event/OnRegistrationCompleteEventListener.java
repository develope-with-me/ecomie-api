package org.csbf.ecomie.event;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.csbf.ecomie.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnRegistrationCompleteEventListener implements ApplicationListener<OnRegistrationCompleteEvent> {

        private final EmailService service;

        @Override
        public void onApplicationEvent(OnRegistrationCompleteEvent event) {
            this.confirmRegistration(event);
        }

        @Async
        public void confirmRegistration(OnRegistrationCompleteEvent event) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            String host;
            if (requestAttributes != null) {
                log.info("Request attributes found {}", requestAttributes.getAttribute("javax.servlet.request.attributes", RequestAttributes.SCOPE_REQUEST));
                host = (String) requestAttributes.getAttribute("host", RequestAttributes.SCOPE_REQUEST);
            } else {
                host = event.getEmail();
            }
            service.sendEmailVerificationToken(host, event.getEmail());
        }
}
