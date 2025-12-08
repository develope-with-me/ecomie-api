package org.csbf.ecomie.service.impl;

import lombok.RequiredArgsConstructor;
import org.csbf.ecomie.config.AuthContext;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.constant.SessionStatus;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.entity.ChallengeEntity;
import org.csbf.ecomie.entity.SessionEntity;
import org.csbf.ecomie.entity.SubscriptionEntity;
import org.csbf.ecomie.entity.UserEntity;
import org.csbf.ecomie.mapper.SubscriptionMapper;
import org.csbf.ecomie.repository.ChallengeRepository;
import org.csbf.ecomie.repository.SessionRepository;
import org.csbf.ecomie.repository.SubscriptionRepository;
import org.csbf.ecomie.repository.UserRepository;
import org.csbf.ecomie.service.SubscriptionService;
import org.csbf.ecomie.utils.helperclasses.HelperDomain;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;
    private final SessionRepository sessionRepo;
    private final ChallengeRepository challengeRepo;
    private final AuthContext authContext;
    private final SubscriptionMapper mapper;


    @Override
    @Transactional
    public HelperDomain.Subscription subscribe(SubscriptionRequest subscriptionRequest) {
        UserEntity userEntity = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());
        return createSubscription(subscriptionRequest, userEntity);

    }



    @Override
    @Transactional
    public HelperDomain.Subscription subscribeUser(UUID id, SubscriptionRequest subscriptionRequest) {
        UserEntity userEntity = userRepo.findById(id).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with id (%s) not found".formatted(id.toString())).toException());
        return createSubscription(subscriptionRequest, userEntity);
    }



    @Override
    @Transactional
    public ResponseMessage removeUserFromSession(UUID sessionId, UUID userId) {
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findBySession_IdAndUser_Id(sessionId, userId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "UserEntity not subscribed to sessionEntity").toException());
        return unsubscribe(subscriptionEntity);
    }


    @Override
    @Transactional
    public ResponseMessage unSubscribeUser(UUID subscriptionId) {
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "SubscriptionEntity with id (%s) not found".formatted(subscriptionId.toString())).toException());
        return unsubscribe(subscriptionEntity);
    }

    private ResponseMessage unsubscribe(SubscriptionEntity subscriptionEntity) {
        SessionEntity sessionEntity = subscriptionEntity.getSession();
        sessionRepo.save(sessionEntity);

        ChallengeEntity challengeEntity = subscriptionEntity.getChallenge();
        challengeRepo.save(challengeEntity);

        subscriptionRepo.delete(subscriptionEntity);
        return new ResponseMessage.SuccessResponseMessage("SubscriptionEntity deleted");
    }

    @Override
    public HelperDomain.Subscription update(UUID subscriptionId, @NotNull SubscriptionRequest subscriptionRequest) {
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "SubscriptionEntity with id (%s) not found".formatted(subscriptionId.toString())).toException());
        if (!subscriptionEntity.getSession().getStatus().equals(SessionStatus.ONGOING)) {
            throw Problems.PAYLOAD_VALIDATION_ERROR.withDetail("Cannot modify subscriptionEntity of sessionEntity that is not ongoing").toException();
        }

        subscriptionEntity.setTarget(subscriptionRequest.target());
        subscriptionEntity.setChallenge(challengeRepo.findById(subscriptionRequest.challengeId()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity.challengeId", "challengeEntity with id (%s) not found".formatted(subscriptionRequest.challengeId())).toException()));

        return mapper.asDomainObject(subscriptionRepo.save(subscriptionEntity));
    }

    @Override
    public HelperDomain.Subscription getSubscription(UUID subscriptionId) {
        var subscription = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "SubscriptionEntity with id (%s) not found".formatted(subscriptionId.toString())).toException());
        if (authContext.getAuthUser().getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals(Role.ADMIN.name()))) {
            if (!subscription.getUser().getEmail().equals(authContext.getAuthUser().getName())) {
                throw Problems.INCONSISTENT_STATE_ERROR.withDetail("UserEntity not subscribed").toException();

            }
        }

                return mapper.asDomainObject(subscription);
    }

    @Override
    public List<HelperDomain.Subscription> getSubscriptions() {
        List<SubscriptionEntity> subscriptionEntityEntities = subscriptionRepo.findAll();
        return mapper.asDomainObjects(subscriptionEntityEntities);
    }

    @Override
    public HelperDomain.Subscription getSessionSubscription(UUID sessionId) {
        UserEntity userEntity = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());
        SessionEntity sessionEntity = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findBySessionAndUser(sessionEntity, userEntity).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "UserEntity not subscribed to sessionEntity").toException());
        return mapper.asDomainObject(subscriptionEntity);
    }


    private HelperDomain.Subscription createSubscription(SubscriptionRequest subscriptionRequest, @NotNull UserEntity userEntity) {
        if(!userEntity.getRole().equals(Role.ECOMIEST)) {
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withProblemError("userEntity", "User not an ECOMIEST").toException();
        }
//        SessionEntity sessionEntity = sessionRepo.findByStatus(SessionStatus.ONGOING).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "No ongoing SessionEntity found").toException());

        if (subscriptionRepo.existsBySession_Status_AndUser_Id(SessionStatus.ONGOING, userEntity.getId())){
//        if (subscriptionRepo.existsBySession_Id_AndUser_Id(sessionEntity.getId(), userEntity.getId())){
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.withDetail("User has already subscribed to this sessionEntity").toException();
        }
        SessionEntity sessionEntity = sessionRepo.findByStatus(SessionStatus.ONGOING).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "No ongoing SessionEntity found").toException());

        ChallengeEntity challengeEntity = challengeRepo.findById(subscriptionRequest.challengeId()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity.challengeId", "ChallengeEntity with id (%s) does not exist".formatted(subscriptionRequest.challengeId().toString())).toException());

        var subscription = SubscriptionEntity.builder()
                .target(subscriptionRequest.target())
                .session(sessionEntity)
                .user(userEntity)
                .challenge(challengeEntity)
                .build();

        subscription = subscriptionRepo.save(subscription);

        return authContext.isAuthorized(Role.ADMIN) ? mapper.asDomainObject(subscription) : mapper.asDomainObject(subscription).justMinimal();
    }


}
