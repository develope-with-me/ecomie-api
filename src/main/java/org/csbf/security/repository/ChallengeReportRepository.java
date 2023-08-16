package org.csbf.security.repository;

import org.csbf.security.model.Challenge;
import org.csbf.security.model.ChallengeReport;
import org.csbf.security.model.Session;
import org.csbf.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ChallengeReportRepository extends JpaRepository<ChallengeReport, UUID> {
    List<ChallengeReport> findAllBySessionAndEcomiestAndChallenge(Session session, User user, Challenge challenge);
    int  countChallengeReportsBySessionAndEcomiestAndChallenge(Session session, User user, Challenge challenge);
    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReport cr where cr.session=:session and cr.ecomiest=:user and cr.challenge=:challenge" )
    int numberAnEcomiestEvangelizedToViaAChallengeInASession(@Param("session") Session session, @Param("user") User user, @Param("challenge") Challenge challenge);
    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReport cr where cr.session=:session and cr.ecomiest=:user")
    int numberAnEcomiestEvangelizedToInASession(@Param("session") Session session, @Param("user") User user);
    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReport cr where cr.ecomiest=:user")
    int numberAnEcomiestEvangelizedTo(@Param("user") User user);
}
