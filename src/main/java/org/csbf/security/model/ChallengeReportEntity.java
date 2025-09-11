package org.csbf.security.model;

import jakarta.persistence.*;
import lombok.*;
import org.csbf.security.repository.ChallengeReportRepository;
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
@Table(name = "challenge_reports")
public class ChallengeReportEntity extends BaseEntity {

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int numberEvangelizedTo;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int numberOfNewConverts;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int numberFollowedUp;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String difficulties;

    @Column(nullable = true)
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    private ChallengeEntity challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private SubscriptionEntity subscription;
}
