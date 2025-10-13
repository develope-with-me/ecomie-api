package org.csbf.security.service.imp;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.ChallengeType;
import org.csbf.security.constant.Role;
import org.csbf.security.exceptions.Problems;
import org.csbf.security.entity.ChallengeEntity;
import org.csbf.security.mapper.ChallengeMapper;
import org.csbf.security.mapper.SessionMapper;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.ChallengeService;
import org.csbf.security.utils.commons.Mapper;
import org.csbf.security.utils.helperclasses.HelperDomain.*;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
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
public class ChallengeServiceImp implements ChallengeService {

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

        addSessionToChallenge(challenge.sessions(), challengeEntity);
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

        addSessionToChallenge(challenge.sessions(), challengeEntity);

        var oldChallenge = mapper.asDomainObject(challengeEntity);
        var oldJsonChallenge = Mapper.toJsonObject(oldChallenge);
        var newJsonChallenge = Mapper.toJsonObject(challenge);
        oldJsonChallenge.putAll(newJsonChallenge);

        challenge = Mapper.fromJsonObject(oldJsonChallenge, Challenge.class);

        var updatedChallenge = challengeRepo.save(mapper.asEntity(challenge));

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

}
