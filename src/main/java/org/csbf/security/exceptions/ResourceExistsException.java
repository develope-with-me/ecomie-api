package org.csbf.security.exceptions;

public class ResourceExistsException extends BaseException {
    public ResourceExistsException(String message) {
        super(message);
    }

    public static class EmailExistsException extends ResourceExistsException {
        public EmailExistsException(String email) {
            super("Email " + email + " already in use");
        }
    }
}