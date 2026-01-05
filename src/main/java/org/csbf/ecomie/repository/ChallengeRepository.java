package org.csbf.ecomie.repository;

import org.csbf.ecomie.entity.ChallengeEntity;
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
