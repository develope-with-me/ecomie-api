package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.Role;
import org.csbf.security.constant.SessionStatus;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.Problems;
import org.csbf.security.entity.ChallengeEntity;
import org.csbf.security.entity.SessionEntity;
import org.csbf.security.entity.SubscriptionEntity;
import org.csbf.security.entity.UserEntity;
import org.csbf.security.mapper.SubscriptionMapper;
import org.csbf.security.mapper.UserMapper;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.SubscriptionService;
import org.csbf.security.utils.helperclasses.HelperDomain.*;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImp implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;
    private final SessionRepository sessionRepo;
    private final ChallengeRepository challengeRepo;
    private final AuthContext authContext;
    private final SubscriptionMapper mapper;


    @Override
    @Transactional
    public Subscription subscribe(SubscriptionCreateDto subscriptionCreateDto) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());
        return createSubscription(subscriptionCreateDto, userEntity);

    }



    @Override
    @Transactional
    public Subscription subscribeUser(UUID id, SubscriptionCreateDto subscriptionCreateDto) {
        UserEntity userEntity = userRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(id.toString())).toException());
        return createSubscription(subscriptionCreateDto, userEntity);
    }



    @Override
    @Transactional
    public ResponseMessage removeUserFromSession(UUID sessionId, UUID userId) {
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findBySession_IdAndUser_Id(sessionId, userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "UserEntity not subscribed to sessionEntity").toException());
        return unSubscribeUser(subscriptionEntity.getId());
    }


    @Override
    @Transactional
    public ResponseMessage unSubscribeUser(UUID subscriptionId) {
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "SubscriptionEntity with id (%s) not found".formatted(subscriptionId.toString())).toException());

        SessionEntity sessionEntity = subscriptionEntity.getSession();
        sessionRepo.save(sessionEntity);

        ChallengeEntity challengeEntity = subscriptionEntity.getChallenge();
        challengeRepo.save(challengeEntity);

        subscriptionRepo.delete(subscriptionEntity);
        return new ResponseMessage.SuccessResponseMessage("SubscriptionEntity deleted");
    }

    @Override
    public Subscription update(UUID subscriptionId, @NotNull SubscriptionCreateDto subscriptionCreateDto) {
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "SubscriptionEntity with id (%s) not found".formatted(subscriptionId.toString())).toException());
        if (!subscriptionEntity.getSession().getStatus().equals(SessionStatus.ONGOING)) {
            throw Problems.PAYLOAD_VALIDATION_ERROR.appendDetail("Cannot modify subscriptionEntity of sessionEntity that is not ongoing").toException();
        }


        subscriptionEntity.setTarget(subscriptionCreateDto.target());
        subscriptionEntity.setChallenge(challengeRepo.findById(subscriptionCreateDto.challengeId()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity.challengeId", "challengeEntity with id (%s) not found".formatted(subscriptionCreateDto.challengeId())).toException()));

        return mapper.asDomainObject(subscriptionRepo.save(subscriptionEntity));
    }

    @Override
    public Subscription getSubscription(UUID subscriptionId) {
        var subscription = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "SubscriptionEntity with id (%s) not found".formatted(subscriptionId.toString())).toException());
        if (authContext.getAuthUser().getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals(Role.ADMIN.name()))) {
            if (!subscription.getUser().getEmail().equals(authContext.getAuthUser().getName())) {
                throw Problems.INCONSISTENT_STATE_ERROR.appendDetail("UserEntity not subscribed").toException();

            }
        }

                return mapper.asDomainObject(subscription);
    }

    @Override
    public List<Subscription> getSubscriptions() {
        List<SubscriptionEntity> subscriptionEntities = subscriptionRepo.findAll();
        return mapper.asDomainObjects(subscriptionEntities);
    }

    @Override
    public Subscription getSessionSubscription(UUID sessionId) {
                Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepo.findByEmail(authUser.getName()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(authUser.getName())).toException());
        SessionEntity sessionEntity = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findBySessionAndUser(sessionEntity, userEntity).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "UserEntity not subscribed to sessionEntity").toException());
        return mapper.asDomainObject(subscriptionEntity);
    }


    private Subscription createSubscription(SubscriptionCreateDto subscriptionCreateDto, @NotNull UserEntity userEntity) {
        if(!userEntity.getRole().equals(Role.ECOMIEST)) {
            throw new BadRequestException("UserEntity not an ECOMIEST");
        }
        if (sessionRepo.existsBySubscriptions_UserId(userEntity.getId())){
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.appendDetail("UserEntity has already subscribed to this sessionEntity").toException();
        }
        SessionEntity sessionEntity = sessionRepo.findByStatus(SessionStatus.ONGOING).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "No ongoing SessionEntity found").toException());
        ChallengeEntity challengeEntity = challengeRepo.findById(subscriptionCreateDto.challengeId()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity.challengeId", "ChallengeEntity with id (%s) does not exist".formatted(subscriptionCreateDto.challengeId().toString())).toException());

        var subscription = SubscriptionEntity.builder()
                .target(subscriptionCreateDto.target())
                .session(sessionEntity)
                .user(userEntity)
                .challenge(challengeEntity)
                .build();

        subscription = subscriptionRepo.save(subscription);

        return mapper.asDomainObject(subscription);
    }


}
