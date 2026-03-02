package org.csbf.ecomie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Slf4j
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
    public Subscription subscribe(SubscriptionRequest subscriptionRequest) {
        UserEntity userEntity = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("userEntity",
                        "User with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());
        return createSubscription(subscriptionRequest, userEntity);

    }

    @Override
    @Transactional
    public Subscription subscribeUser(UUID id, SubscriptionRequest subscriptionRequest) {
        UserEntity userEntity = userRepo.findById(id).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("userEntity",
                        "User with id (%s) not found".formatted(id.toString())).toException());
        return createSubscription(subscriptionRequest, userEntity);
    }



    @Override
    @Transactional
    public ResponseMessage<Subscription> removeUserFromSession(UUID sessionId, UUID userId) {
        List<SubscriptionEntity> subscriptionEntities = subscriptionRepo.findAllBySession_IdAndUser_Id(sessionId, userId);
        if (subscriptionEntities.isEmpty()) {
            throw  Problems.NOT_FOUND.withProblemError("subscriptionEntity",
                    "User not subscribed to session").toException();
        }

        return unsubscribe(subscriptionEntities);
    }


    @Override
    @Transactional
    public ResponseMessage<Subscription> unSubscribeUser(UUID subscriptionId) {
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findById(subscriptionId).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("subscriptionEntity",
                        "Subscription with id (%s) not found".formatted(subscriptionId.toString())).toException());
        return unsubscribe(subscriptionEntity);
    }

    private ResponseMessage<Subscription> unsubscribe(SubscriptionEntity subscriptionEntity) {

        // TODO: Find out why I wrote these comment lines innitially
//        SessionEntity sessionEntity = subscriptionEntity.getSession();
//        sessionRepo.save (sessionEntity);
//
//        ChallengeEntity challengeEntity = subscriptionEntity.getChallenge();
//        challengeRepo.save(challengeEntity);

        subscriptionRepo.delete(subscriptionEntity);
        return new ResponseMessage.SuccessResponseMessage<>("Subscription deleted");
    }

    private ResponseMessage<Subscription> unsubscribe(List<SubscriptionEntity> subscriptionEntities) {
        subscriptionRepo.deleteAll(subscriptionEntities);
        return new ResponseMessage.SuccessResponseMessage<>("Subscription deleted");
    }

    @Override
    public Subscription update(UUID subscriptionId, @NotNull SubscriptionRequest subscriptionRequest) {
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findById(subscriptionId).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("subscriptionEntity",
                        "Subscription with id (%s) not found".formatted(subscriptionId.toString())).toException());
        if (!subscriptionEntity.getSession().getStatus().equals(SessionStatus.ONGOING)) {
            throw Problems.PAYLOAD_VALIDATION_ERROR
                    .withDetail("Cannot modify subscription of session that is not ongoing").toException();
        }
        ChallengeEntity challengeEntity = challengeRepo.findById(subscriptionRequest.challengeId()).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("subscriptionEntity.challengeId",
                        "Challenge with id (%s) does not exist"
                                .formatted(subscriptionRequest.challengeId().toString())).toException());

        if (subscriptionEntity.getSession().getChallenges().stream().noneMatch(challengeEntity::equals)) {
            throw Problems.NOT_FOUND.withDetail("Challenge (id = %s) is not part of current current session (id = %s)"
                    .formatted(challengeEntity.getId(), subscriptionEntity.getSession().getId())).toException();
        }

        subscriptionEntity.setTarget(subscriptionRequest.target());
        subscriptionEntity.setChallenge(challengeRepo.findById(subscriptionRequest.challengeId()).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("subscriptionEntity.challengeId",
                        "challenge with id (%s) not found".formatted(subscriptionRequest.challengeId())).toException()));

        return mapper.asDomainObject(subscriptionRepo.save(subscriptionEntity));
    }

    @Override
    public Subscription getSubscription(UUID subscriptionId) {
        var subscription = subscriptionRepo.findById(subscriptionId).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("subscriptionEntity",
                        "Subscription with id (%s) not found".formatted(subscriptionId.toString())).toException());
        if (!authContext.isAuthorized(Role.ADMIN)) {
            if (!subscription.getUser().getEmail().equals(authContext.getAuthUser().getName())) {
                throw Problems.INCONSISTENT_STATE_ERROR.withDetail("User not subscribed").toException();
            }
        }

        return mapper.asDomainObject(subscription);
    }

    @Override
    public List<Subscription> getSubscriptions(boolean isOngoing) {
        List<SubscriptionEntity> subscriptionEntities = new ArrayList<>();
        if(authContext.isAuthorized(Role.ADMIN)) {
            subscriptionEntities = isOngoing
                    ? subscriptionRepo.findAllBySession_Status(SessionStatus.ONGOING)
                    : subscriptionRepo.findAll();
        } else {
            String userEmail = authContext.getAuthUser().getName();
            subscriptionEntities = isOngoing
                    ?  subscriptionRepo.findAllBySession_StatusAndUser_Email(SessionStatus.ONGOING, userEmail)
                    : subscriptionRepo.findAllByUser_Email(userEmail);
        }

        return mapper.asDomainObjects(subscriptionEntities);
    }

    @Override
    public List<Subscription> getSessionSubscription(UUID sessionId) {
        List<SubscriptionEntity> subscriptions = new ArrayList<>();
        if(authContext.isAuthorized(Role.ADMIN)) {
            subscriptions = subscriptionRepo.findAllBySession_id(sessionId);
        } else {
            UserEntity userEntity = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(
                    () -> Problems.NOT_FOUND.withProblemError("userEntity",
                            "User with email (%s) not found"
                                    .formatted(authContext.getAuthUser().getName())).toException());
            SessionEntity sessionEntity = sessionRepo.findById(sessionId).orElseThrow(
                    () -> Problems.NOT_FOUND.withProblemError("sessionEntity",
                            "Session with id (%s) not found".formatted(sessionId.toString())).toException());
            subscriptions = subscriptionRepo.findAllBySessionAndUser(sessionEntity, userEntity);
            if(subscriptions.isEmpty()) {
                throw Problems.NOT_FOUND.withProblemError("subscriptionEntity",
                        "User not subscribed to session").toException();
            }

        }
        return mapper.asDomainObjects(subscriptions);
    }

    @Override
    public ResponseMessage<Subscription> toggleBlock(UUID id) {
        log.info("SubscriptionServiceImpl.toggleBlock");
        var subscription = subscriptionRepo.findById(id).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("subscriptionEntity",
                        "Subscription with id (%s) not found".formatted(id.toString())).toException());

        subscription.setBlocked(!subscription.getBlocked());
        SubscriptionEntity updatedSub = subscriptionRepo.save(subscription);

        return new ResponseMessage.SuccessResponseMessage<>(subscription.getBlocked() ? "User Subscription Blocked"
                : "User Subscription Unblocked",
                mapper.asDomainObject(updatedSub));
    }


    private Subscription createSubscription(SubscriptionRequest subscriptionRequest, @NotNull UserEntity userEntity) {
        if(!userEntity.getRole().equals(Role.ECOMIEST)) {
            throw Problems.FORBIDDEN_OPERATION_ERROR.withProblemError("userEntity",
                    "User not an ECOMIEST").toException();
        }
//        SessionEntity sessionEntity = sessionRepo.findByStatus(SessionStatus.ONGOING).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "No ongoing Session found").toException());

        ChallengeEntity challengeEntity = challengeRepo.findById(subscriptionRequest.challengeId()).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("subscriptionEntity.challengeId",
                        "Challenge with id (%s) does not exist"
                                .formatted(subscriptionRequest.challengeId().toString())).toException());

        if (subscriptionRequest.target() > challengeEntity.getTarget()) {
            throw Problems.BAD_REQUEST
                    .withDetail("Target must be greater than or equal to %s".formatted(Integer.toString(challengeEntity.getTarget()))).toException();
        }

        if (subscriptionRepo.existsBySession_Status_AndUser_IdAndChallenge_Type(SessionStatus.ONGOING, userEntity.getId(), challengeEntity.getType())){
//        if (subscriptionRepo.existsBySession_Id_AndUser_Id(sessionEntity.getId(), userEntity.getId())){
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR
                    .withDetail("User has already subscribed to this session").toException();
        }
        SessionEntity sessionEntity = sessionRepo.findByStatus(SessionStatus.ONGOING).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("sessionEntity",
                        "No ongoing Session found").toException());



        if (sessionEntity.getChallenges().stream().noneMatch(challengeEntity::equals)) {
            throw Problems.NOT_FOUND.withDetail("Challenge (id = %s) is not part of current current session (id = %s)"
                    .formatted(challengeEntity.getId(), sessionEntity.getId())).toException();
        }

        var subscription = SubscriptionEntity.builder()
                .target(subscriptionRequest.target())
                .session(sessionEntity)
                .user(userEntity)
                .blocked(false)
                .challenge(challengeEntity)
                .build();

        subscription = subscriptionRepo.save(subscription);

        return authContext.isAuthorized(Role.ADMIN)
                ? mapper.asDomainObject(subscription)
                : mapper.asDomainObject(subscription).justMinimal();
    }


}
