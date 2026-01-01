package org.csbf.ecomie.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.ecomie.config.AuthContext;
import org.csbf.ecomie.constant.ChallengeType;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.entity.SessionEntity;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.entity.ChallengeEntity;
import org.csbf.ecomie.mapper.ChallengeMapper;
import org.csbf.ecomie.repository.ChallengeRepository;
import org.csbf.ecomie.repository.SessionRepository;
import org.csbf.ecomie.repository.SubscriptionRepository;
import org.csbf.ecomie.repository.UserRepository;
import org.csbf.ecomie.service.ChallengeService;
import org.csbf.ecomie.utils.commons.Mapper;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepo;
    private final SessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final AuthContext authContext;
    private final ChallengeMapper mapper;


    @Override
    @Transactional
    public ResponseMessage store(Challenge challenge) {

        if (!EnumUtils.isValidEnum(ChallengeType.class, challenge.type().toUpperCase())) {
            throw Problems.BAD_REQUEST.withProblemError("challengeEntity.type", "Invalid challengeEntity type (%s)".formatted(challenge.type())).toException();
        }
        var type = challenge.type().toUpperCase();
        challengeRepo.findByName(challenge.name()).ifPresent(_ -> {throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("challengeEntity.name", "ChallengeEntity with name (%s) already exist".formatted(challenge.name())).toException();});
        var challengeEntity = ChallengeEntity.builder()
                .name(challenge.name())
                .description(challenge.description())
                .type(ChallengeType.valueOf(type))
                .target(challenge.target())
                .build();

        if (challenge.sessions() != null) {
            addSessionToChallenge(challenge.sessions(), challengeEntity);
        }
        challengeRepo.save(challengeEntity);
        return new ResponseMessage.SuccessResponseMessage("ChallengeEntity created. Type: " + type);

    }

    @Override
    public ResponseMessage changeType(UUID id, String type) {
        var challenge = challengeRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(id.toString())).toException());
        if (!EnumUtils.isValidEnum(ChallengeType.class, type.toUpperCase())) {
            throw Problems.BAD_REQUEST.withProblemError("challengeEntity.type", "Invalid challengeEntity type (%s)".formatted(type)).toException();
        }
        challenge.setType(ChallengeType.valueOf(type.toUpperCase()));

        var updatedChallenge = challengeRepo.save(challenge);
        return new ResponseMessage.SuccessResponseMessage("ChallengeEntity type changed. Type: " + updatedChallenge.getType());
    }

    @Override
    @Transactional
    public ResponseMessage update(UUID challengeId, Challenge challenge) {
        var challengeEntity = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
        challengeRepo.findByName(challenge.name()).ifPresent(chal -> {
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("challengeEntity.name", "ChallengeEntity with the name (%s) already exist".formatted(chal.getName())).toException();
        });

        if (!EnumUtils.isValidEnum(ChallengeType.class, challenge.type().toUpperCase())) {
            throw Problems.BAD_REQUEST.withProblemError("challengeEntity.type", "Invalid challengeEntity type (%s)".formatted(challenge.type())).toException();
        }

        if (challenge.sessions() != null) {
            addSessionToChallenge(challenge.sessions(), challengeEntity);
        }

        List<SessionEntity> sessions = null;
        if (challengeEntity.getSessions() != null) {
            sessions = challengeEntity.getSessions();
            challengeEntity.setSessions(null);
        }
        var oldChallenge = mapper.asDomainObject(challengeEntity);
        var oldJsonChallenge = Mapper.toJsonObject(oldChallenge);
        var newJsonChallenge = Mapper.toJsonObject(challenge);

        challenge = Mapper.withUpdateValuesOnly(oldJsonChallenge, newJsonChallenge, Challenge.class);

        challengeEntity = mapper.asEntity(challenge);
        if (sessions != null) {
            challengeEntity.setSessions(sessions);
        }
        var updatedChallenge = challengeRepo.save(challengeEntity);

        return new ResponseMessage.SuccessResponseMessage("ChallengeEntity updated. Type: " + updatedChallenge.getType());
    }

    private ChallengeEntity addSessionToChallenge(List<Session> sessions, ChallengeEntity challengeEntity) {
        var sessionIds = sessions.stream().map(Session::id).toList();
        if (!sessionIds.isEmpty()) {
            sessionIds.forEach(id -> {
                var session = sessionRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(id.toString())).toException());
                challengeEntity.addSession(session);
            });
        }
        return challengeEntity;
    }

    @Override
    public Challenge getChallenge(UUID challengeId) {
        Authentication authUser = authContext.getAuthUser();
        var challengeEntity = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
        var challenge = mapper.asDomainObject(challengeEntity);
        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))){
            return challenge.justMinimal();
        }

        return challenge;
    }

    @Override
    public List<Challenge> getChallenges() {
        Authentication authUser = authContext.getAuthUser();

        var challengeEntities = challengeRepo.findAll();
        var challenges = mapper.asDomainObjects(challengeEntities);

        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))){
            return challenges.stream().map(Challenge::justMinimal).collect(Collectors.toList());

        }

        return challenges;
    }

    @Override
    public ResponseMessage deleteChallenge(UUID id) {
        challengeRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(id.toString())).toException());
        challengeRepo.deleteById(id);
        return new ResponseMessage.SuccessResponseMessage("Challenge deleted successfully");
    }

}
