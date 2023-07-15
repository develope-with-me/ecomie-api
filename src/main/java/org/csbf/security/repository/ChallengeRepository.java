package org.csbf.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChallengeRepository extends JpaRepository<ChallengeRepository, UUID> {
}
