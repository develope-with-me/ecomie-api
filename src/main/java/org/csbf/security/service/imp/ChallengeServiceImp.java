package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.ChallengeType;
import org.csbf.security.constant.Role;
import org.csbf.security.exceptions.Problems;
import org.csbf.security.model.ChallengeEntity;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.ChallengeService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


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

    @Override
    @Transactional
    public ResponseMessage store(HelperDto.ChallengeCreateDto challengeCreateDto) {

        if (!EnumUtils.isValidEnum(ChallengeType.class, challengeCreateDto.type().toUpperCase())) {
            throw Problems.BAD_REQUEST.withProblemError("challengeEntity.type", "Invalid challengeEntity type (%s)".formatted(challengeCreateDto.type())).toException();
        }
        var type = challengeCreateDto.type().toUpperCase();
        challengeRepo.findByName(challengeCreateDto.name()).ifPresent(challenge -> {throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("challengeEntity.name", "ChallengeEntity with name (%s) already exist".formatted(challengeCreateDto.name())).toException();});
        var challenge = ChallengeEntity.builder()
                .name(challengeCreateDto.name())
                .description(challengeCreateDto.description())
                .type(ChallengeType.valueOf(type))
                .target(challengeCreateDto.target())
                .build();

        addSessionToChallenge(challengeCreateDto.sessions(), challenge);
        challengeRepo.save(challenge);
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
    public ResponseMessage update(UUID challengeId, HelperDto.ChallengeCreateDto challengeCreateDto) {
        var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());
        challengeRepo.findByName(challengeCreateDto.name()).ifPresent(chal -> {
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("challengeEntity.name", "ChallengeEntity with the name (%s) already exist".formatted(chal.getName())).toException();
        });

        if (!EnumUtils.isValidEnum(ChallengeType.class, challengeCreateDto.type().toUpperCase())) {
            throw Problems.BAD_REQUEST.withProblemError("challengeEntity.type", "Invalid challengeEntity type (%s)".formatted(challengeCreateDto.type())).toException();
        }

        challenge.setName(challengeCreateDto.name());
        challenge.setDescription(challenge.getDescription());
        challenge.setTarget(challengeCreateDto.target());

        addSessionToChallenge(challengeCreateDto.sessions(), challenge);
        challengeRepo.save(challenge);

        return new ResponseMessage.SuccessResponseMessage("ChallengeEntity updated. Type: " + challenge.getType());

    }

    private ChallengeEntity addSessionToChallenge(UUID[] sessionIds, ChallengeEntity challengeEntity) {
        if (sessionIds.length > 0) {
            Arrays.asList(sessionIds).forEach(id -> {
                var session = sessionRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(id.toString())).toException());
                challengeEntity.addSession(session);
            });
        }
        return challengeEntity;
    }

    @Override
    public HelperDto.ChallengeFullDto getChallenge(UUID challengeId) {
        Authentication authUser = authContext.getAuthUser();
        var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("challengeEntity", "ChallengeEntity with id (%s) not found".formatted(challengeId.toString())).toException());

        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))){
//            var userEntity = userRepo.findByEmail(authUser.getName()).orElseThrow(()->new ResourceNotFoundException("UserEntity not found"));
//            if (subscriptionRepo.findAllByUserAndChallenge(userEntity, challengeEntity).isEmpty()) {
//                throw new BadRequestException.InvalidAuthenticationRequestException("UserEntity not subscribed");
//            }
            return HelperDto.ChallengeFullDto.justMinimal(challenge);
        }

        return new HelperDto.ChallengeFullDto(challenge);
    }

    @Override
    public List<HelperDto.ChallengeFullDto> getChallenges() {
        Authentication authUser = authContext.getAuthUser();

        var challenges = challengeRepo.findAll();
        ArrayList challengeFullDtos = new ArrayList<HelperDto.ChallengeFullDto>();

        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))){
//            var userEntity = userRepo.findByEmail(authUser.getName()).orElseThrow(()->new ResourceNotFoundException("UserEntity not found"));
            challenges.forEach(challenge -> {
//                if (subscriptionRepo.findAllByUserAndChallenge(userEntity, challengeEntity).isEmpty()) {
//                    throw new BadRequestException.InvalidAuthenticationRequestException("UserEntity not subscribed");
//                }
                challengeFullDtos.add(HelperDto.ChallengeFullDto.justMinimal(challenge));
            });
            return challengeFullDtos;

        }

        challenges.forEach(challenge -> challengeFullDtos.add(new HelperDto.ChallengeFullDto(challenge)));

        return challengeFullDtos;
    }

}
