package org.csbf.ecomie.repository;

import org.csbf.ecomie.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface ChallengeReportRepository extends JpaRepository<ChallengeReportEntity, UUID> {
    List<ChallengeReportEntity> findAllBySubscription(SubscriptionEntity subscriptionEntity);
    Optional<ChallengeReportEntity> findByIdAndUser_Email(UUID uuid, String userEmail);
    List<ChallengeReportEntity> findAllByUser_Email(String userEmail);
    List<ChallengeReportEntity> findAllBySubscription_Challenge_Id(UUID subscriptionChallengeId);

    List<ChallengeReportEntity> findAllByUser_EmailAndSubscription_Challenge_Id(String userEmail, UUID subscriptionChallengeId);

    //    int  countChallengeReportsBySessionAndEcomiestAndChallenge(SessionEntity sessionEntity, UserEntity userEntity, ChallengeEntity challengeEntity);
    int  countAllBySubscription(SubscriptionEntity subscriptionEntity);
//    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReportEntity cr where cr.sessionEntity=:sessionEntity and cr.ecomiest=:userEntity and cr.challengeEntity=:challengeEntity" )
//    int numberAnEcomiestEvangelizedToViaAChallengeInASession(@Param("sessionEntity") SessionEntity sessionEntity, @Param("userEntity") UserEntity userEntity, @Param("challengeEntity") ChallengeEntity challengeEntity);
//    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReportEntity cr where cr.sessionEntity=:sessionEntity and cr.ecomiest=:userEntity")
//    int numberAnEcomiestEvangelizedToInASession(@Param("sessionEntity") SessionEntity sessionEntity, @Param("userEntity") UserEntity userEntity);
//    @Query(value = "SELECT sum(cr.numberEvangelizedTo) from ChallengeReportEntity cr where cr.ecomiest=:userEntity")
//    int numberAnEcomiestEvangelizedTo(@Param("userEntity") UserEntity userEntity);
}
