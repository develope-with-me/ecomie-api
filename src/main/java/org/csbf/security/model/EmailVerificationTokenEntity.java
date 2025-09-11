package org.csbf.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csbf.security.utils.commons.BaseEntity;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationTokenEntity extends BaseEntity {

    private static final int EXPIRATION = 60 * 24;

    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;

    public EmailVerificationTokenEntity(UserEntity user) {
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}
