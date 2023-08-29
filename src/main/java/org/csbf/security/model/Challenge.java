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
    private int target;
    @Column(nullable = true)
    @OneToMany(mappedBy = "challenge")
    private List<Subscription> subscriptions;
//    @Column(nullable = true)
//    @OneToMany(mappedBy = "challenge")
//    private List<ChallengeReport> challengeReports;
    @Column(nullable = true)
    @ManyToMany(mappedBy = "challenges")
    private List<Session> sessions;
    private String type;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public LocalDateTime[] getTargetDeadLines(){
        return null;
    }


    public void addSubscription(Subscription subscription) {
        this.subscriptions.add(subscription);
        subscription.setChallenge(this);
    }

    public void removeSubscription(Subscription subscription) {
        this.subscriptions.remove(subscription);
        subscription.setChallenge(null);
    }
//    public void addChallengeReport(ChallengeReport report) {
//        this.challengeReports.add(report);
//        report.setChallenge(this);
//    }
//
//    public void removeChallengeReport(ChallengeReport report) {
//        this.challengeReports.remove(report);
//        report.setChallenge(null);
//    }

    public void addSession(Session session) {
        this.sessions.add(session);
        session.getChallenges().add(this);
    }

    public void removeSession(Session session) {
        this.sessions.remove(session);
        session.getChallenges().remove(this);
    }
}