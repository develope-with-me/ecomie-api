package org.csbf.security.repository;

import org.csbf.security.model.Challenge;
import org.csbf.security.model.ChallengeReport;
import org.csbf.security.model.Session;
import org.csbf.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.UUID;

public interface ChallengeReportRepository extends JpaRepository<ChallengeReport, UUID> {
    ArrayList<ChallengeReport> findChallengeReportsBySessionAndEcomistAndChallenge(Session session, User user, Challenge challenge);
    int countChallengeReportsBySessionAndEcomistAndChallenge(Session session, User user, Challenge challenge);
    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReport cr where cr.session=:session and cr.ecomist=:user and cr.challenge=:challenge" )
    int numberAnEcomistEvangelizedToViaAChallengeInASession(@Param("session") Session session, @Param("user") User user, @Param("challenge") Challenge challenge);
    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReport cr where cr.session=:session and cr.ecomist=:user")
    int numberAnEcomistEvangelizedToInASession(@Param("session") Session session, @Param("user") User user);
    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReport cr where cr.ecomist=:user")
    int numberAnEcomistEvangelizedTo(@Param("user") User user);
}
