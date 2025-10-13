package org.csbf.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csbf.security.constant.ChallengeType;
import org.csbf.security.utils.commons.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;


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
@Table(name = "challenges")
public class ChallengeEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private int target;

    @Enumerated(EnumType.STRING)
    private ChallengeType type;


    @Column(nullable = true)
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sessions")
    private List<SessionEntity> sessions;


    public LocalDateTime[] getTargetDeadLines(){
        return null;
    }


    public void addSession(SessionEntity sessionEntity) {
        this.sessions.add(sessionEntity);
        sessionEntity.getChallenges().add(this);
    }

    public void removeSession(SessionEntity sessionEntity) {
        this.sessions.remove(sessionEntity);
        sessionEntity.getChallenges().remove(this);
    }
}
