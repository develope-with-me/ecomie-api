package org.csbf.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = true)
    private String description;
    @Column(nullable = false)
    private long target;
    @Column(nullable = true)
    @OneToMany(mappedBy = "challenge")
    private List<Subscription> subscriptions;
    @Column(nullable = true)
    @OneToMany(mappedBy = "challenge")
    private List<ChallengeReport> challengeReports;
    @Column(nullable = false)
    @ManyToMany(mappedBy = "challenges")
    private List<Session> sessions;
    private String type;
    @Immutable
    private LocalDateTime startDate;
    @Immutable
    private LocalDateTime endDate;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public LocalDateTime[] getTargetDeadLines(){
        return null;
    }

}
