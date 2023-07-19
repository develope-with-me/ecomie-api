package org.csbf.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csbf.security.constant.SessionStatus;
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
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = true)
    private String description;
    @Column(nullable = true)
    @OneToMany(mappedBy = "session")
    private List<ChallengeReport> challengeReports;
    @ManyToMany
    @Column(nullable = true)
    @JoinTable(
            name = "session_challenge",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "challenge_id"))
    private List<Challenge> challenges;
    @Column(nullable = true)
    @OneToMany(mappedBy = "session")
    private List<Subscription> subscriptions;
    @Immutable
    private LocalDateTime startDate;
    @Immutable
    private LocalDateTime endDate;
    @Column(nullable = false, columnDefinition = "varchar(255) default INACTIVE")
    private String status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
