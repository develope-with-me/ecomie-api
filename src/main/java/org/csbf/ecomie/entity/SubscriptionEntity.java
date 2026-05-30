package org.csbf.ecomie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.constant.SubscriptionStatus;
import org.csbf.ecomie.exceptions.Problem;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.utils.commons.BaseEntity;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscriptions")
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class SubscriptionEntity extends BaseEntity {

    @Column(unique = false, nullable = false)
    private int target;

    @Column(unique = false, nullable = false)
    private Boolean blocked;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    private ChallengeEntity challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private SessionEntity session;
//
//    public void setUser(UserEntity user) {
//        if(user.getRole() != Role.ECOMIEST) {
//            throw Problems.BAD_REQUEST.withProblemError("user", "User with specified id (%s) not an ECOMIEST".formatted(user.getId())).toException();
//        }
//        this.user = user;
//    }
}
