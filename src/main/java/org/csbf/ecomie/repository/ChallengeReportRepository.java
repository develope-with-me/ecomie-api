package org.csbf.ecomie.repository;

import org.csbf.ecomie.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface ChallengeReportRepository extends JpaRepository<ChallengeReportEntity, UUID> {
    List<ChallengeReportEntity> findAllBySubscription(SubscriptionEntity subscriptionEntity);
//    int  countChallengeReportsBySessionAndEcomiestAndChallenge(SessionEntity sessionEntity, UserEntity userEntity, ChallengeEntity challengeEntity);
    int  countAllBySubscription(SubscriptionEntity subscriptionEntity);
//    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReportEntity cr where cr.sessionEntity=:sessionEntity and cr.ecomiest=:userEntity and cr.challengeEntity=:challengeEntity" )
//    int numberAnEcomiestEvangelizedToViaAChallengeInASession(@Param("sessionEntity") SessionEntity sessionEntity, @Param("userEntity") UserEntity userEntity, @Param("challengeEntity") ChallengeEntity challengeEntity);
//    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReportEntity cr where cr.sessionEntity=:sessionEntity and cr.ecomiest=:userEntity")
//    int numberAnEcomiestEvangelizedToInASession(@Param("sessionEntity") SessionEntity sessionEntity, @Param("userEntity") UserEntity userEntity);
//    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReportEntity cr where cr.ecomiest=:userEntity")
//    int numberAnEcomiestEvangelizedTo(@Param("userEntity") UserEntity userEntity);
}
