package org.csbf.ecomie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.exceptions.Problems;
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
@Table(name = "challenge_reports")
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ChallengeReportEntity extends BaseEntity {

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int numberEvangelizedTo;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int numberOfNewConverts;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int numberFollowedUp;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String difficulties;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private SubscriptionEntity subscription;

    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;


    public void setUser(UserEntity user) {
        if(user.getRole() != Role.ECOMIEST) {
            throw Problems.BAD_REQUEST.withProblemError("user", "User with specified id (%s) not an ECOMIEST".formatted(user.getId())).toException();
        }
        this.user = user;
    }
}
