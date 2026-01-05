package org.csbf.ecomie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.UUID;

/**
 * Configuration class for token-related properties.
 * This class reads properties from application.properties and makes them
 * available as static fields that can be accessed by JPA entities.
 */
@Component
public class EntityConfigParams {

    @Value("${email-verification.token.duration}")
    private int emailVerificationTokenDuration;

    @Value("${password-reset.token.duration}")
    private int passwordResetTokenDuration;

    @Value( "${anonymous-user-id}")
    private UUID anonymousUserId;

    // Static fields to be accessed by entities
    private static int EMAIL_VERIFICATION_TOKEN_DURATION_IN_MINUTES;
    private static int PASSWORD_RESET_TOKEN_DURATION_IN_MINUTES;
    private static UUID ANONYMOUS_USER_ID;

    /**
     * Initialize static fields after dependency injection.
     */
    @PostConstruct
    public void init() {
        EMAIL_VERIFICATION_TOKEN_DURATION_IN_MINUTES = emailVerificationTokenDuration;
        PASSWORD_RESET_TOKEN_DURATION_IN_MINUTES = passwordResetTokenDuration;
        ANONYMOUS_USER_ID = anonymousUserId;
    }

    /**
     * Get the duration of email verification tokens in minutes.
     * @return The duration in minutes
     */
    public static int getEmailVerificationTokenDuration() {
        return EMAIL_VERIFICATION_TOKEN_DURATION_IN_MINUTES;
    }

    /**
     * Get the duration of password reset tokens in minutes.
     * @return The duration in minutes
     */
    public static int getPasswordResetTokenDuration() {
        return PASSWORD_RESET_TOKEN_DURATION_IN_MINUTES;
    }

    /**
     * Get the id of an anonymous user.
     * @return The UUID
     */
    public static UUID getAnonymousUserId() {
        return ANONYMOUS_USER_ID;
    }
}