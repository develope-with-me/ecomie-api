package org.csbf.security.repository;

import org.csbf.security.entity.ChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface ChallengeRepository extends JpaRepository<ChallengeEntity, UUID> {
    Optional<ChallengeEntity> findByName(String name);
}
