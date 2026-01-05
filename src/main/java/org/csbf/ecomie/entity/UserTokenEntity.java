package org.csbf.ecomie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.csbf.ecomie.config.EntityConfigParams;
import org.csbf.ecomie.constant.TokenType;
import org.csbf.ecomie.utils.commons.BaseEntity;

import java.time.LocalDateTime;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_tokens")
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class UserTokenEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private UserEntity user;

    private LocalDateTime expiryDate;

    private Boolean isValid;

    public void setExpiryDate(int expiryTimeInMinutes) {
        this.expiryDate = createdOn().plusMinutes(expiryTimeInMinutes);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isBefore(expiryDate);
    };

    @Override
    public void prePersist() {
        super.prePersist();
        var expiryTimeInMinutes = type.name().equalsIgnoreCase("EMAIL_VERIFICATION")
                ? EntityConfigParams.getEmailVerificationTokenDuration()
                : EntityConfigParams.getPasswordResetTokenDuration();
        setExpiryDate(expiryTimeInMinutes);
        isValid = true;
    }
}
