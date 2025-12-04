package org.csbf.ecomie.entity;

import jakarta.persistence.*;
import lombok.*;
import org.csbf.ecomie.constant.TokenType;
import org.csbf.ecomie.utils.commons.BaseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_tokens")
public class UserTokenEntity extends BaseEntity {

//    @Transient
//    private final Environment env;

    @Transient
    @Value("${email-verification.token.duration}")
    private int EMAIL_VERIFICATION_TOKEN_DURATION_IN_MINUTES;

    @Transient
    @Value("${password-reset.token.duration}")
    private int PASSWORD_RESET_TOKEN_DURATION_IN_MINUTES;
//    private final int EMAIL_VERIFICATION_TOKEN_DURATION_IN_MINUTES = Integer.parseInt(Objects.requireNonNull(env.getProperty("email-verification.token.duration")));
//    private final int PASSWORD_RESET_TOKEN_DURATION_IN_MINUTES = Integer.parseInt(Objects.requireNonNull(env.getProperty("password-reset.token.duration")));


    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user")
    private UserEntity user;

    private LocalDateTime expiryDate;

    private Boolean isValid;

    public void setExpiryDate(int expiryTimeInMinutes) {
        this.expiryDate = createdAt().plusMinutes(expiryTimeInMinutes);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isBefore(expiryDate);
    };

    @Override
    public void prePersist() {
        super.prePersist();
        var expiryTimeInMinutes = type.name().equalsIgnoreCase("EMAIL_VERIFICATION")
                ? EMAIL_VERIFICATION_TOKEN_DURATION_IN_MINUTES
                : PASSWORD_RESET_TOKEN_DURATION_IN_MINUTES;
        setExpiryDate(expiryTimeInMinutes);
        isValid = true;
    }
}
