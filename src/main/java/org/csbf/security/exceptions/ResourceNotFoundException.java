package org.csbf.security.exceptions;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static class EmailNotFoundException extends ResourceNotFoundException {
        public EmailNotFoundException(String email) {
            super("User with email " + email + " not found");
        }
    }

    public static class OtpNotFoundException extends ResourceNotFoundException {
        public OtpNotFoundException() {
            super("Invalid otp code");
        }
    }
}