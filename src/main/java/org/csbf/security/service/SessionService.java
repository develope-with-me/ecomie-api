package org.csbf.security.service;

import org.csbf.security.utils.helperclasses.HelperDomain.*;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface SessionService {
    ResponseMessage store(Session session);

    ResponseMessage changeStatus(UUID id, String status);

    ResponseMessage update(UUID id, Session session);

    Session getSession(UUID id);

    List<Session> getSessions();

    Session assignChallenges(UUID sessionId, List<UUID> challengeIds);

    @Transactional
    Session removeChallenges(UUID sessionId, List<UUID> challengeIds);

    List<Session> getUserSessions(UUID userId);

    Session assignChallenge(UUID sessionId, UUID challengeId);

    Session removeChallenge(UUID sessionId, UUID challengeId);
}
