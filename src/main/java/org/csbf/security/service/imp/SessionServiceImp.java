package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.Role;
import org.csbf.security.constant.SessionStatus;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.Problems;
import org.csbf.security.model.SessionEntity;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.SessionService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Service
@RequiredArgsConstructor
public class SessionServiceImp implements SessionService {
    private final SessionRepository sessionRepo;
    private final ChallengeRepository challengeRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;
    private final AuthContext authContext;


    @Override
    public ResponseMessage store(HelperDto.SessionCreateDto sessionCreateDto) {
        sessionRepo.findByName(sessionCreateDto.name()).ifPresent(session -> {
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("sessionEntity", "SessionEntity %s exists".formatted(session.getName())).toException();
        });
        var session = SessionEntity.builder()
                .name(sessionCreateDto.name())
                .description(sessionCreateDto.description())
                .startDate(sessionCreateDto.startDate())
                .endDate(sessionCreateDto.endDate())
                .status(SessionStatus.INACTIVE)
                .build();
        var createdSession = sessionRepo.save(session);
        return new ResponseMessage.SuccessResponseMessage("SessionEntity created. Status: " + createdSession.getStatus());
    }

    @Override
    public ResponseMessage changeStatus(UUID id, String status) {
        var session = sessionRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(id.toString())).toException());

        String statusUpperCase = status.toUpperCase();
        if (!EnumUtils.isValidEnum(SessionStatus.class, statusUpperCase)) {
            throw new BadRequestException("Invalid sessionEntity status");
        }
        if ( SessionStatus.ONGOING.name().equals(statusUpperCase) && sessionRepo.existsByStatus(SessionStatus.valueOf(statusUpperCase))) {
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("sessionEntity", "Only one sessionEntity can be active at a time. Please end currently ongoing sessionEntity first").toException();
        }
        session.setStatus(SessionStatus.valueOf(statusUpperCase));

        var updatedSession = sessionRepo.save(session);
        return new ResponseMessage.SuccessResponseMessage("SessionEntity status changed. Status: " + updatedSession.getStatus());
    }

    @Override
    public ResponseMessage update(UUID id, HelperDto.SessionCreateDto sessionCreateDto) {
        var session = sessionRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(id.toString())).toException());
        sessionRepo.findByName(sessionCreateDto.name()).ifPresent(ses -> {
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("sessionEntity", "SessionEntity %s exists".formatted(ses.getName())).toException();
        });

        session.setName(sessionCreateDto.name());
        session.setDescription(sessionCreateDto.description());
        if (session.getStatus() != SessionStatus.ONGOING) {
            if (sessionCreateDto.startDate() != null)
                session.setStartDate(sessionCreateDto.startDate());
            if (sessionCreateDto.endDate() != null)
                session.setEndDate(sessionCreateDto.endDate());
        }

        var updatedSession = sessionRepo.save(session);
        return new ResponseMessage.SuccessResponseMessage("SessionEntity updated. Status: " + updatedSession.getStatus());
    }

    @Override
    public HelperDto.SessionFullDto getSession(UUID id) {
        Authentication authUser = authContext.getAuthUser();
        var session = sessionRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(id.toString())).toException());

        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))){
//            var userEntity = userRepo.findByEmail(authUser.getName()).orElseThrow(()->new ResourceNotFoundException("UserEntity not found"));
//            if (subscriptionRepo.findAllByUserAndChallenge(userEntity, challengeEntity).isEmpty()) {
//                throw new BadRequestException.InvalidAuthenticationRequestException("UserEntity not subscribed");
//            }
            return HelperDto.SessionFullDto.justMinimal(session);
        }
        return new HelperDto.SessionFullDto(session);
    }

    @Override
    public List<HelperDto.SessionFullDto> getSessions() {
        Authentication authUser = authContext.getAuthUser();
        var sessions = sessionRepo.findAll();
        ArrayList sessionDtos = new ArrayList<HelperDto.SessionFullDto>();

        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))) {
//            var userEntity = userRepo.findByEmail(authUser.getName()).orElseThrow(()->new ResourceNotFoundException("UserEntity not found"));
            sessions.forEach(session -> {
//                if (subscriptionRepo.findAllByUserAndChallenge(userEntity, challengeEntity).isEmpty()) {
//                    throw new BadRequestException.InvalidAuthenticationRequestException("UserEntity not subscribed");
//                }
                sessionDtos.add(HelperDto.SessionFullDto.justMinimal(session));
            });
            return sessionDtos;
        }

        sessions.forEach(session -> sessionDtos.add(new HelperDto.SessionFullDto(session)));

        return sessionDtos;
    }

    @Override
    @Transactional
    public HelperDto.SessionFullDto assignChallenges(UUID sessionId, UUID[] challengeIds) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());

        Arrays.asList(challengeIds).forEach(challengeId -> {
            var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
            session.addChallenge(challenge);
        });

        return new HelperDto.SessionFullDto(sessionRepo.save(session));
    }

    @Override
    @Transactional
    public HelperDto.SessionFullDto assignChallenge(UUID sessionId, UUID challengeId) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());

        var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
        session.addChallenge(challenge);

        return new HelperDto.SessionFullDto(sessionRepo.save(session));
    }

    @Override
    @Transactional
    public HelperDto.SessionFullDto removeChallenges(UUID sessionId, UUID[] challengeIds) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());

        Arrays.asList(challengeIds).forEach(challengeId -> {
            var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
            session.removeChallenge(challenge);
        });

        return new HelperDto.SessionFullDto(sessionRepo.save(session));
    }

    @Override
    @Transactional
    public HelperDto.SessionFullDto removeChallenge(UUID sessionId, UUID challengeId) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());
        var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
        session.removeChallenge(challenge);

        return new HelperDto.SessionFullDto(sessionRepo.save(session));
    }

    @Override
    public List<HelperDto.SessionFullDto> getUserSessions(UUID userId) {
        var user = userRepo.findById(userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(userId.toString())).toException());
        ArrayList sessionDtos = new ArrayList<HelperDto.SessionFullDto>();
        subscriptionRepo.selectAllSessionsThisUserHasSubscribedTo(user).forEach(session -> {
            sessionDtos.add(new HelperDto.SessionFullDto(session));
        });

        return sessionDtos;
    }

}
