package org.csbf.ecomie.repository;

import org.csbf.ecomie.constant.SessionStatus;
import org.csbf.ecomie.entity.SessionEntity;
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
    boolean existsByStatus(SessionStatus status);
}
