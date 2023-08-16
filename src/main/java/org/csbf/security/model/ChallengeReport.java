package org.csbf.security.model;

import jakarta.persistence.*;
import lombok.*;
import org.csbf.security.repository.ChallengeReportRepository;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@RequiredArgsConstructor
@Table(name = "challenge_report")
public class ChallengeReport {
    @Transient
    ChallengeReportRepository reportRepo;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;
//    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User ecomiest;
//    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Session session;
//    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Subscription subscription;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int numberEvangelizedTo;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int numberOfNewConverts;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int numberFollowedUp;
    @Column(nullable = true)
    private String difficulties;
    @Column(nullable = true)
    private String remark;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public int getTotalNumberThisEcomistEvangelizedToViaThisChallengeInThisSession() {
        return reportRepo.numberAnEcomiestEvangelizedToViaAChallengeInASession(this.session,this.ecomiest, this.challenge);
    }

    public int getTotalNumberLeftToMeetTarget() {
        LocalDateTime now = LocalDateTime.now();

        return this.challenge.getTarget() -  getTotalNumberThisEcomistEvangelizedToViaThisChallengeInThisSession();
    }

    public boolean isCompleted() {
        return (getTotalNumberThisEcomistEvangelizedToViaThisChallengeInThisSession() == this.challenge.getTarget());
    }
}
