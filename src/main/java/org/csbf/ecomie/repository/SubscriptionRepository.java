package org.csbf.ecomie.repository;

import org.csbf.ecomie.constant.SessionStatus;
import org.csbf.ecomie.entity.ChallengeEntity;
import org.csbf.ecomie.entity.SessionEntity;
import org.csbf.ecomie.entity.SubscriptionEntity;
import org.csbf.ecomie.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {
    Optional<SubscriptionEntity> findByIdAndUser(UUID subscriptionId, UserEntity userEntity);
    List<SubscriptionEntity> findAllByUserAndChallenge(UserEntity userEntity, ChallengeEntity challengeEntity);
    List<SubscriptionEntity> findAllBySession_id(UUID sessionId);
    List<SubscriptionEntity> findAllByUser_Email(String userEmail);
    Optional<SubscriptionEntity> findBySession(SessionEntity sessionEntity);
    Optional<SubscriptionEntity> findBySessionAndBlocked(SessionEntity sessionEntity, boolean blocked);
    Optional<SubscriptionEntity> findBySessionAndUser(SessionEntity sessionEntity, UserEntity userEntity);
    Optional<SubscriptionEntity> findBySession_IdAndUser_Id(UUID sessionId, UUID userId);
    Optional<SubscriptionEntity> findBySessionAndUserAndBlocked(SessionEntity sessionEntity, UserEntity userEntity, boolean blocked);
    Optional<SubscriptionEntity> findBySessionAndChallenge(SessionEntity sessionEntity, ChallengeEntity challengeEntity);
    Optional<SubscriptionEntity> findBySessionAndChallengeAndUser(SessionEntity sessionEntity, ChallengeEntity challengeEntity, UserEntity userEntity);
    Optional<SubscriptionEntity> findBySessionAndChallengeAndUserAndBlocked(SessionEntity sessionEntity, ChallengeEntity challengeEntity, UserEntity userEntity, boolean blocked);
    boolean existsBySession_Id(UUID sessionId);

    boolean existsBySession_Id_AndUser_Id(UUID sessionId, UUID userId);
    boolean existsBySession_Status_AndUser_Id(SessionStatus sessionStatus, UUID userId);

    @Query(value = "SELECT sub.session from SubscriptionEntity sub where sub.user=:user")
    List<SessionEntity> selectAllSessionsThisUserHasSubscribedTo(@Param("user") UserEntity userEntity);
    @Query(value = "SELECT sub.session from SubscriptionEntity sub where sub.session=:session and sub.blocked=true")
    List<UserEntity> selectAllUsersBlockedInSession(@Param("session") SessionEntity sessionEntity);
    @Query(value = "SELECT sub.user from SubscriptionEntity sub where sub.session=:session and sub.challenge=:challenge")
    List<UserEntity> selectAllUsersSubscribedToSessionViaChallenge(@Param("session") SessionEntity sessionEntity, @Param("challenge") ChallengeEntity challengeEntity);
    @Query(value = "SELECT sub.user from SubscriptionEntity sub where sub.session=:session")
    List<UserEntity> selectAllUsersSubscribedToSession(@Param("session") SessionEntity sessionEntity);

    List<SubscriptionEntity> findAllByUser_Id(UUID userId);
}
