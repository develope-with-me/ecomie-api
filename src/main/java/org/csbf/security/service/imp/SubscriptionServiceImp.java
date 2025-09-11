package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.Role;
import org.csbf.security.constant.SessionStatus;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.Problems;
import org.csbf.security.model.ChallengeEntity;
import org.csbf.security.model.SessionEntity;
import org.csbf.security.model.SubscriptionEntity;
import org.csbf.security.model.UserEntity;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.SubscriptionService;
import org.csbf.security.utils.helperclasses.HelperDto;
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

    @Override
    @Transactional
    public HelperDto.SubscriptionFullDto subscribe(HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(authContext.getAuthUser().getName())).toException());
        return createSubscription(subscriptionCreateDto, userEntity);

    }



    @Override
    @Transactional
    public HelperDto.SubscriptionFullDto subscribeUser(UUID id, HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
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

        SessionEntity sessionEntity = subscriptionEntity.getSessionEntity();
        sessionEntity.removeSubscription(subscriptionEntity);
        sessionRepo.save(sessionEntity);

        ChallengeEntity challengeEntity = subscriptionEntity.getChallengeEntity();
        challengeEntity.removeSubscription(subscriptionEntity);
        challengeRepo.save(challengeEntity);

        subscriptionRepo.delete(subscriptionEntity);
        return new ResponseMessage.SuccessResponseMessage("SubscriptionEntity deleted");
    }

    @Override
    public HelperDto.SubscriptionFullDto update(UUID subscriptionId,HelperDto.@NotNull SubscriptionCreateDto subscriptionCreateDto) {
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "SubscriptionEntity with id (%s) not found".formatted(subscriptionId.toString())).toException());
        if (!subscriptionEntity.getSessionEntity().getStatus().equals(SessionStatus.ONGOING)) {
            throw Problems.PAYLOAD_VALIDATION_ERROR.appendDetail("Cannot modify subscriptionEntity of sessionEntity that is not ongoing").toException();
        }


        subscriptionEntity.setTarget(subscriptionCreateDto.target());
        subscriptionEntity.setChallengeEntity(challengeRepo.findById(subscriptionCreateDto.challengeId()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity.challengeId", "challengeEntity with id (%s) not found".formatted(subscriptionCreateDto.challengeId())).toException()));

        return new HelperDto.SubscriptionFullDto(subscriptionRepo.save(subscriptionEntity));
    }

    @Override
    public HelperDto.SubscriptionFullDto getSubscription(UUID subscriptionId) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var subscription = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "SubscriptionEntity with id (%s) not found".formatted(subscriptionId.toString())).toException());
//        if (!authUser.getAuthorities().contains("ADMIN")) {
        if (authContext.getAuthUser().getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals(Role.ADMIN.name()))) {
            if (!subscription.getUserEntity().getEmail().equals(authContext.getAuthUser().getName())) {
                throw Problems.INCONSISTENT_STATE_ERROR.appendDetail("UserEntity not subscribed").toException();

            }
        }

                return new HelperDto.SubscriptionFullDto(subscription);
    }

    @Override
    public List<HelperDto.SubscriptionFullDto> getSubscriptions() {
        List subscriptionFullDtos = new ArrayList<HelperDto.SubscriptionFullDto>();
        subscriptionRepo.findAll().forEach(sub -> {subscriptionFullDtos.add(new HelperDto.SubscriptionFullDto(sub));});
        return subscriptionFullDtos;
    }

    @Override
    public HelperDto.SubscriptionFullDto getSessionSubscription(UUID sessionId) {
                Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepo.findByEmail(authUser.getName()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("userEntity", "UserEntity with email (%s) not found".formatted(authUser.getName())).toException());
        SessionEntity sessionEntity = sessionRepo.findById(sessionId).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "SessionEntity with id (%s) not found".formatted(sessionId.toString())).toException());
        SubscriptionEntity subscriptionEntity = subscriptionRepo.findBySessionAndUser(sessionEntity, userEntity).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity", "UserEntity not subscribed to sessionEntity").toException());
        return new HelperDto.SubscriptionFullDto(subscriptionEntity);
    }


    private HelperDto.SubscriptionFullDto createSubscription(HelperDto.SubscriptionCreateDto subscriptionCreateDto, @NotNull UserEntity userEntity) {
        if(!userEntity.getRole().equals(Role.ECOMIEST)) {
            throw new BadRequestException("UserEntity not an ECOMIEST");
        }
        if (sessionRepo.existsBySubscriptions_UserId(userEntity.getId())){
            throw Problems.UNIQUE_CONSTRAINT_VIOLATION_ERROR.appendDetail("UserEntity has already subscribed to this sessionEntity").toException();
        }
        SessionEntity sessionEntity = sessionRepo.findByStatus(SessionStatus.ONGOING).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("sessionEntity", "No ongoing SessionEntity found").toException());
        ChallengeEntity challengeEntity = challengeRepo.findById(subscriptionCreateDto.challengeId()).orElseThrow(() -> Problems.NOT_FOUND.withProblemError("subscriptionEntity.challengeId", "ChallengeEntity with id (%s) does not exist".formatted(subscriptionCreateDto.challengeId().toString())).toException());

        var subscription = SubscriptionEntity.builder()
//                .id(UUID.randomUUID())
                .target(subscriptionCreateDto.target())
                .sessionEntity(sessionEntity)
                .userEntity(userEntity)
                .challengeEntity(challengeEntity)
                .build();

//        sessionEntity.addSubscription(subscriptionEntity);
//        sessionEntity = sessionRepo.save(sessionEntity);
//        challengeEntity.addSubscription(subscriptionEntity);
//        challengeEntity = challengeRepo.save(challengeEntity);
//        userEntity.addSubscription(subscriptionEntity);
//        userEntity = userRepo.save(userEntity);

        subscription = subscriptionRepo.save(subscription);

        return new HelperDto.SubscriptionFullDto(subscription);
    }


}
