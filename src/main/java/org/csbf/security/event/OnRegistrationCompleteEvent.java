package org.csbf.security.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
//    private String appUrl;
//    private Locale locale;
//    private User user;


//    public OnRegistrationCompleteEvent(User user, Locale locale, String appUrl) {
//        super(user);
//
//        this.user = user;
//        this.locale = locale;
//        this.appUrl = appUrl;
//    }
//
//    public String getAppUrl() {
//        return appUrl;
//    }
//
//    public void setAppUrl(String appUrl) {
//        this.appUrl = appUrl;
//    }
//
//    public Locale getLocale() {
//        return locale;
//    }
//
//    public void setLocale(Locale locale) {
//        this.locale = locale;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
        private String requestHeaderHost;
        private String email;
        public OnRegistrationCompleteEvent(String requestHeaderHost, String email) {
                super(requestHeaderHost);
                this.requestHeaderHost = requestHeaderHost;
                this.email = email;
    }
}
