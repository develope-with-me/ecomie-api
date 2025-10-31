package org.csbf.ecomie.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
        private String requestHeaderHost;
        private String email;
        public OnRegistrationCompleteEvent(String requestHeaderHost, String email) {
                super(requestHeaderHost);
                this.requestHeaderHost = requestHeaderHost;
                this.email = email;
    }
}
