package org.csbf.security.repository;

import org.csbf.security.constant.SessionStatus;
import org.csbf.security.model.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
    Optional<SessionEntity> findByName(String name);
    Optional<SessionEntity> findByStatus(SessionStatus status);
    List<SessionEntity> findSessionsByChallenges_Id(UUID challengeId);
    Optional<SessionEntity> findBySubscriptions_UserId(UUID subscriptionsUserId);
    boolean existsBySubscriptions_UserId(UUID subscriptionsUserId);
    boolean existsByStatus(SessionStatus status);
}
