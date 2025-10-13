package org.csbf.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csbf.security.utils.commons.BaseEntity;


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
@Table(name = "subscriptions")
public class SubscriptionEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private int target;

    @Column(unique = true, nullable = false)
    private boolean blocked;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    private ChallengeEntity challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private SessionEntity session;
}
