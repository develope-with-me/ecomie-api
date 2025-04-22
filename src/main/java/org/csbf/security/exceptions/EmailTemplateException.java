package org.csbf.security.exceptions;

public class    EmailTemplateException extends BaseException {
    public EmailTemplateException(String message) {
        super(message);
    }

    public static class SignUpTemplateException extends EmailTemplateException {
        public SignUpTemplateException(String email) {
            super("Email " + email + " already in use");
        }
    }
}