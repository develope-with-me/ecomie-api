package org.csbf.ecomie.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class OnRoleChangeRequestEvent extends ApplicationEvent {
    private String requestHeaderHost;
    private String email;
    private String role;
    public OnRoleChangeRequestEvent(String requestHeaderHost, String email, String role) {
        super(requestHeaderHost);
        this.requestHeaderHost = requestHeaderHost;
        this.email = email;
        this.role = role;
    }
}
