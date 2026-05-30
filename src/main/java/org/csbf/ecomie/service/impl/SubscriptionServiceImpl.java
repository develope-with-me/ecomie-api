package org.csbf.ecomie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.ecomie.config.AuthContext;
import org.csbf.ecomie.constant.ChallengeType;
import org.csbf.ecomie.constant.Role;
import org.csbf.ecomie.constant.SessionStatus;
import org.csbf.ecomie.constant.SubscriptionStatus;
import org.csbf.ecomie.exceptions.Problems;
import org.csbf.ecomie.entity.ChallengeEntity;
import org.csbf.ecomie.entity.SessionEntity;
import org.csbf.ecomie.entity.SubscriptionEntity;
import org.csbf.ecomie.entity.UserEntity;
import org.csbf.ecomie.mapper.SubscriptionMapper;
import org.csbf.ecomie.repository.*;
import org.csbf.ecomie.service.SubscriptionService;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final ChallengeReportRepository reportRepo;



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

        if (reportRepo.existsBySubscription_Id(subscriptionEntity.id())) {
            throw Problems.FORBIDDEN_OPERATION_ERROR.withDetail(
                    "Cannot unsubscribe till the end of the session as you have associated reports").toException();
        }

        subscriptionEntity.setStatus(SubscriptionStatus.CANCELED);

        subscriptionRepo.save(subscriptionEntity);
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

        if(!authContext.isAuthorized(Role.ADMIN)) {
            if(Objects.isNull(subscriptionEntity.getUser()) || !subscriptionEntity.getUser().getEmail().equals(authContext.getAuthUser().getName())) {
                throw Problems.FORBIDDEN_OPERATION_ERROR.withDetail("Subscription is not yours").toException();
            }
        }

        if (reportRepo.existsBySubscription_Id(subscriptionEntity.id())) {
            throw Problems.FORBIDDEN_OPERATION_ERROR.withDetail(
                    "Current subscription cannot be updated as it already has associated reports").toException();
        }

        if (!subscriptionEntity.getSession().getStatus().equals(SessionStatus.ONGOING)) {
            throw Problems.PAYLOAD_VALIDATION_ERROR
                    .withDetail("Cannot modify subscription of session that is not ongoing").toException();
        }

        ChallengeEntity challengeEntity = challengeRepo.findById(subscriptionRequest.challengeId()).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("challengeId",
                        "Challenge with id (%s) does not exist"
                                .formatted(subscriptionRequest.challengeId().toString())).toException());

        if (Objects.nonNull(subscriptionEntity.getSession()) &&
                Objects.nonNull(subscriptionEntity.getSession().getChallenges()) &&
                subscriptionEntity.getSession().getChallenges().stream().noneMatch(challengeEntity::equals)) {
            throw Problems.NOT_FOUND.withDetail("Challenge (id = %s) is not part of current current session (id = %s)"
                    .formatted(challengeEntity.getId(), subscriptionEntity.getSession().getId())).toException();
        }

        if (subscriptionRequest.target() != challengeEntity.getTarget()) {
            throw Problems.BAD_REQUEST
                    .withProblemError("target", "Target must be  equal to %s".formatted(Integer.toString(challengeEntity.getTarget()))).toException();
        }



        subscriptionEntity.setTarget(subscriptionRequest.target());
        subscriptionEntity.setChallenge(challengeEntity);

        if(authContext.isAuthorized(Role.ADMIN) && Objects.nonNull(subscriptionRequest.userId())) {
            UserEntity user = userRepo.findById(subscriptionRequest.userId()).orElseThrow(() ->
                    Problems.NOT_FOUND.withProblemError("userId",
                            "User with specified id (%s) not found".formatted(subscriptionRequest.userId())).toException());

            subscriptionEntity.setUser(user);
            if(!user.getRole().equals(Role.ECOMIEST)) {
                subscriptionEntity.setStatus(SubscriptionStatus.PENDING);
            }
        }

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

    @Override
    public ResponseMessage<Subscription> updateStatus(UUID id, String status) {
        log.info("SubscriptionServiceImpl.updateStatus");
        var subscription = subscriptionRepo.findById(id).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("subscriptionEntity",
                        "Subscription with id (%s) not found".formatted(id.toString())).toException());

        if(!EnumUtils.isValidEnum(SubscriptionStatus.class, status.toUpperCase())) {
            final String[] enums = {""};
            EnumUtils.getEnumList(SubscriptionStatus.class).stream().map(Enum::toString).toList()
                    .forEach(str -> {
                        enums[0] = enums[0] +str+", ";
                    });
            throw Problems.INCONSISTENT_DATA_ERROR.withProblemError("status",
                            "%s not a valid status. [ %s ]".formatted(status, enums[0]))
                    .toException();
        }

        subscription.setStatus(SubscriptionStatus.valueOf(status.toUpperCase()));
        SubscriptionEntity updatedSub = subscriptionRepo.save(subscription);

        return new ResponseMessage.SuccessResponseMessage<>("Subscription status updated",
                mapper.asDomainObject(updatedSub));
    }


    private Subscription createSubscription(SubscriptionRequest subscriptionRequest, @NotNull UserEntity userEntity) {
//        SessionEntity sessionEntity = sessionRepo.findByStatus(SessionStatus.ONGOING).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "No ongoing Session found").toException());

        ChallengeEntity challengeEntity = challengeRepo.findById(subscriptionRequest.challengeId()).orElseThrow(
                () -> Problems.NOT_FOUND.withProblemError("subscriptionEntity.challengeId",
                        "Challenge with id (%s) does not exist"
                                .formatted(subscriptionRequest.challengeId().toString())).toException());

        if (subscriptionRequest.target() != challengeEntity.getTarget()) {
            throw Problems.BAD_REQUEST
                    .withProblemError("target", "Target must be  equal to %s".formatted(Integer.toString(challengeEntity.getTarget()))).toException();
        }

        if (subscriptionRepo.existsBySession_Status_AndUser_IdAndChallenge_Id(SessionStatus.ONGOING, userEntity.getId(), challengeEntity.getId())) {
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR
                    .withDetail("User has already subscribed to this session's Challenge").toException();

        }

        if (subscriptionRepo.existsBySession_Status_AndUser_IdAndChallenge_Type(SessionStatus.ONGOING, userEntity.getId(), challengeEntity.getType())) {
            if (!challengeEntity.getType().equals(ChallengeType.EVENT)) {
                throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR
                        .withDetail("User has already subscribed to a NORMAL Challenge in this session").toException();
            }
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
                .status(SubscriptionStatus.ACTIVE)
                .blocked(false)
                .challenge(challengeEntity)
                .build();

        if(!userEntity.getRole().equals(Role.ECOMIEST)) {
            subscription.setStatus(SubscriptionStatus.PENDING);
        }

        subscription = subscriptionRepo.save(subscription);

        return authContext.isAuthorized(Role.ADMIN)
                ? mapper.asDomainObject(subscription)
                : mapper.asDomainObject(subscription).justMinimal();
    }


}
