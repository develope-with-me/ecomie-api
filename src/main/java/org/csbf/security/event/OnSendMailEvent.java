package org.csbf.security.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class OnSendMailEvent extends ApplicationEvent {
    public OnSendMailEvent(Object source) {
        super(source);
    }
}
