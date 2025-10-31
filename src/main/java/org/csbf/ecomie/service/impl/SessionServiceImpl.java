package org.csbf.ecomie.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.ecomie.config.AuthContext;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.constant.SessionStatus;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.mapper.SessionMapper;
import org.csbf.ecomie.repository.ChallengeRepository;
import org.csbf.ecomie.repository.SessionRepository;
import org.csbf.ecomie.repository.SubscriptionRepository;
import org.csbf.ecomie.repository.UserRepository;
import org.csbf.ecomie.service.SessionService;
import org.csbf.ecomie.utils.commons.Mapper;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepo;
    private final ChallengeRepository challengeRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;
    private final AuthContext authContext;
    private final SessionMapper mapper;


    @Override
    public SuccessResponseMessage store(Session session) {
        sessionRepo.findByName(session.name()).ifPresent(sessionEntity -> {
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("sessionEntity", "SessionEntity %s exists".formatted(sessionEntity.getName())).toException();
        });
        var sessionEntity = mapper.asEntity(session);
        sessionEntity.setStatus(SessionStatus.INACTIVE);

        var createdSession = sessionRepo.save(sessionEntity);
        return new SuccessResponseMessage("SessionEntity created. Status: " + createdSession.getStatus());
    }

    @Override
    public SuccessResponseMessage changeStatus(UUID id, String status) {
        var session = sessionRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(id.toString())).toException());

        String statusUpperCase = status.toUpperCase();
        if (!EnumUtils.isValidEnum(SessionStatus.class, statusUpperCase)) {
            throw Problems.BAD_REQUEST.withProblemError("status", "Invalid sessionEntity status(%s)".formatted(status)).toException();
        }
        if ( SessionStatus.ONGOING.name().equals(statusUpperCase) && sessionRepo.existsByStatus(SessionStatus.valueOf(statusUpperCase))) {
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("sessionEntity", "Only one sessionEntity can be active at a time. Please end currently ongoing sessionEntity first").toException();
        }
        session.setStatus(SessionStatus.valueOf(statusUpperCase));

        var updatedSession = sessionRepo.save(session);
        return new SuccessResponseMessage("SessionEntity status changed. Status: " + updatedSession.getStatus());
    }

    @Override
    public SuccessResponseMessage update(UUID id, Session session) {
        var sessionEntity = sessionRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(id.toString())).toException());
        sessionRepo.findByName(session.name()).ifPresent(ses -> {
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("sessionEntity", "SessionEntity %s exists".formatted(ses.getName())).toException();
        });

        var oldSession = mapper.asDomainObject(sessionEntity);
        var oldJsonSession = Mapper.toJsonObject(oldSession);
        var newJsonSession = Mapper.toJsonObject(session);
        oldJsonSession.putAll(newJsonSession);
        session = Mapper.fromJsonObject(oldJsonSession, Session.class);

        var updatedSession = sessionRepo.save(mapper.asEntity(session));
        return new SuccessResponseMessage("SessionEntity updated. Status: " + updatedSession.getStatus());
    }

    @Override
    public Session getSession(UUID id) {
        Authentication authUser = authContext.getAuthUser();
        var sessionEntity = sessionRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(id.toString())).toException());
        var session = mapper.asDomainObject(sessionEntity);
        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))){
            return session.justMinimal();
        }
        return session;
    }

    @Override
    public List<Session> getSessions() {
        Authentication authUser = authContext.getAuthUser();
        var sessionEntities = sessionRepo.findAll();
        var sessions = mapper.asDomainObjects(sessionEntities);
        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))) {
            List<Session> minimalSessions = new ArrayList<>();
            sessions.forEach(session -> minimalSessions.add(session.justMinimal()));
            return minimalSessions;
        }

        return sessions;
    }

    @Override
//    @Transactional
    public Session assignChallenges(UUID sessionId, List<UUID> challengeIds) {
        var sessionEntity = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());

        challengeIds.forEach(challengeId -> {
            var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
            sessionEntity.addChallenge(challenge);
        });

        return mapper.asDomainObject(sessionRepo.save(sessionEntity));
    }

    @Override
//    @Transactional
    public Session assignChallenge(UUID sessionId, UUID challengeId) {
        var sessionEntity = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());

        var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
        sessionEntity.addChallenge(challenge);

        return mapper.asDomainObject(sessionRepo.save(sessionEntity));
    }

    @Override
//    @Transactional
    public Session removeChallenges(UUID sessionId, List<UUID> challengeIds) {
        var sessionEntity = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());

        challengeIds.forEach(challengeId -> {
            var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
            sessionEntity.removeChallenge(challenge);
        });

        return mapper.asDomainObject(sessionRepo.save(sessionEntity));
    }

    @Override
//    @Transactional
    public Session removeChallenge(UUID sessionId, UUID challengeId) {
        var sessionEntity = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());
        var challengeEntity = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
        sessionEntity.removeChallenge(challengeEntity);

        return mapper.asDomainObject(sessionRepo.save(sessionEntity)) ;
    }

    @Override
    public List<Session> getUserSessions(UUID userId) {
        var user = userRepo.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(userId.toString())).toException());
        var sessionEntities = subscriptionRepo.selectAllSessionsThisUserHasSubscribedTo(user);
        return mapper.asDomainObjects(sessionEntities);
    }

}
