package org.csbf.ecomie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.csbf.ecomie.constant.ChallengeType;
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
@Table(name = "challenges")
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ChallengeEntity extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private int target;

    @Enumerated(EnumType.STRING)
    private ChallengeType type;


    @JsonIgnore
    @Column(nullable = true)
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "challenges")
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
