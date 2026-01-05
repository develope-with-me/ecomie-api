package org.csbf.ecomie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.csbf.ecomie.constant.SessionStatus;
import org.csbf.ecomie.utils.commons.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sessions")
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class SessionEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'INACTIVE'")
    private SessionStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @Column(nullable = true)
    @JoinTable(
            name = "session_challenge",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "challenge_id"))
    private List<ChallengeEntity> challenges;

    public void addChallenge(ChallengeEntity challengeEntity) {
        if (this.challenges.stream().anyMatch(challenge1 -> challenge1.getId().equals(challengeEntity.getId()))) {
            return;
        }
        this.challenges.add(challengeEntity);
        challengeEntity.getSessions().add(this);
    }

    public void removeChallenge(ChallengeEntity challengeEntity) {
        this.challenges.remove(challengeEntity);
        challengeEntity.getSessions().remove(this);
    }
}

