package org.csbf.security.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
//    private String appUrl;
//    private Locale locale;
//    private UserEntity userEntity;


//    public OnRegistrationCompleteEvent(UserEntity userEntity, Locale locale, String appUrl) {
//        super(userEntity);
//
//        this.userEntity = userEntity;
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
//    public UserEntity getUserEntity() {
//        return userEntity;
//    }
//
//    public void setUserEntity(UserEntity userEntity) {
//        this.userEntity = userEntity;
//    }
        private String requestHeaderHost;
        private String email;
        public OnRegistrationCompleteEvent(String requestHeaderHost, String email) {
                super(requestHeaderHost);
                this.requestHeaderHost = requestHeaderHost;
                this.email = email;
    }
}
