package org.csbf.security.utils.helperclasses;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.csbf.security.config.AuthContext;
import org.csbf.security.model.*;
import org.csbf.security.utils.commons.Domain;
import org.csbf.security.utils.commons.ExtendedEmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@RequiredArgsConstructor
public class HelperDto {



    @Builder
    public record RegisterRequest(String firstname, String lastname, @ExtendedEmailValidator String email, @Size(min=8) String password) {
    }

    @Builder
    public record AuthenticationRequest(@ExtendedEmailValidator String email,  String password) {
    }

    @Builder
    public record AuthenticationResponse(String token, String message, boolean success, UserBasicDto user) {
        public AuthenticationResponse(String token, String message, boolean success) {
            this(token, message, success, null);
        }
    }

    @Builder
    public record ConfirmEmailResponse(String token, ResponseMessage responseMessage) {

    }

    @Builder
    public record UserBasicDto(UUID id, String firstName, String lastName, String email, String role, boolean accountEnabled,
                               boolean accountBlocked, boolean accountSoftDeleted) {
    }

    @Builder
    public record UserDto(String firstName, String lastName, String email, String role, String phoneNumber, String country,
                          String region, String city, String language, String profilePictureFileName) {
        public UserDto(UserEntity userEntity) {
            this(userEntity.getFirstName(), userEntity.getLastName(), userEntity.getEmail(), userEntity.getRole().name(), userEntity.getPhoneNumber(), userEntity.getCountry(),
                    userEntity.getRegion(), userEntity.getCity(), userEntity.getLanguage(), userEntity.getProfilePictureFileName());
        }
    }

    @Builder
    public record UserFullDto(UUID id, String firstName, String lastName, String email, String role, String phoneNumber,
                              String country, String region, String city, String language,
                              String profilePictureFileName, boolean accountEnabled, boolean accountBlocked,
                              boolean accountSoftDeleted) {
        public UserFullDto(UserEntity userEntity) {
            this(userEntity.getId(), userEntity.getFirstName(), userEntity.getLastName(), userEntity.getEmail(), userEntity.getRole().name(), userEntity.getPhoneNumber(),
                    userEntity.getCountry(), userEntity.getRegion(), userEntity.getCity(), userEntity.getLanguage(),
                    userEntity.getProfilePictureFileName(), userEntity.isAccountEnabled(), userEntity.isAccountBlocked(),
                    userEntity.isAccountSoftDeleted());
        }
    }

    @Builder
    public record User(UUID id, String firstName, String lastName, String email, String role, String phoneNumber,
                       String country, String region, String city, String language,
                       String profilePictureFileName, boolean accountEnabled, String emailVerificationToken, boolean accountBlocked,
                       boolean accountSoftDeleted, LocalDateTime createdAt, LocalDateTime updatedAt, UUID createdBy, UUID updatedBy) implements Domain {


        @Override
        public String name() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public String alternateName() {
            return "";
        }

//        public static User justMinimal(UserEntity userEntity) {
//           return new User(userEntity.getId(), userEntity.getFirstName(), userEntity.getLastName(), userEntity.getEmail(),
//                    userEntity.getRole().name(), userEntity.getPhoneNumber(), userEntity.getCountry(),
//                    userEntity.getRegion(), userEntity.getCity(), userEntity.getLanguage(),
//                    userEntity.getProfilePictureFileName(), userEntity.isAccountEnabled(), userEntity.getEmailVerificationToken(),
//                   userEntity.isAccountBlocked(), userEntity.isAccountSoftDeleted(), userEntity.createdAt(), userEntity.updatedAt(),
//                    userEntity.createdBy(), userEntity.updatedBy());
//        }
    }

    @Builder
    public record MinimalUser(UUID id, String firstName, String lastName, String email, String phoneNumber,
                       String country, String region, String city, String language,
                       String profilePictureFileName,LocalDateTime createdAt, LocalDateTime updatedAt, UUID createdBy, UUID updatedBy) implements Domain {

        @Override
        public String name() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public String alternateName() {
            return "";
        }
    }

    @Builder
    public record UpdateUserRole(String email, String role) {
    }


    public record ResendVerificationEmailDTO(@ExtendedEmailValidator String email) {
    }

    public record UpdateUserProfileRequest(String firstName, String lastName, String phoneNumber, String country,
                                           String region, String city, String language) {
    }

    public record EmailRequest(String email) {
    }


    /**
     * SessionEntity Helper DTO
     */
    @Builder
    public record SessionFullDto(UUID id, String name, String description, List<ChallengeFullDto> challenges,
                                 LocalDateTime startDate, LocalDateTime endDate,
                                 String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        public SessionFullDto(SessionEntity sessionEntity) {
            this(sessionEntity.getId(), sessionEntity.getName(), sessionEntity.getDescription(), getMinimalChallenges(sessionEntity.getChallenges()),
                    sessionEntity.getStartDate(), sessionEntity.getEndDate(), sessionEntity.getStatus().name(),
                    sessionEntity.getCreatedAt(), sessionEntity.getUpdatedAt());
        }

        public static SessionFullDto justMinimal(SessionEntity sessionEntity) {
            return new SessionFullDto(sessionEntity.getId(), sessionEntity.getName(), null,
                    null, sessionEntity.getStartDate(), sessionEntity.getEndDate(), sessionEntity.getStatus().name(),
                    sessionEntity.getCreatedAt(), sessionEntity.getUpdatedAt());
        }
    }

    @Builder
    public record SessionCreateDto(String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
    }

    public static List<SessionFullDto> getMinimalSessions(List<SessionEntity> sessionEntities) {
        if(Objects.nonNull(sessionEntities)) {
            List<SessionFullDto> sessionDtos = new ArrayList<>();
            sessionEntities.forEach(session -> sessionDtos.add(SessionFullDto.justMinimal(session)));
            return sessionDtos;
        }
        return null;
    }

    @Builder
    public record Session(UUID id, String name, String description, List<Challenge> challenges,
                                 List<Subscription> subscriptions, LocalDateTime startDate, LocalDateTime endDate,
                                 String status, LocalDateTime createdAt, LocalDateTime updatedAt, UUID createdBy, UUID updatedBy) implements Domain{

        @Override
        public String alternateName() {
            return "";
        }

        public static Session justMinimal(SessionEntity sessionEntity) {
            return new Session(sessionEntity.getId(), sessionEntity.getName(), sessionEntity.getDescription(), null,
                    null, sessionEntity.getStartDate(), sessionEntity.getEndDate(), sessionEntity.getStatus().name(),
                    sessionEntity.getCreatedAt(), sessionEntity.getUpdatedAt(), sessionEntity.getCreatedBy(), sessionEntity.getUpdatedBy());
        }
    }


    /**
     * ChallengeEntity Helper DTO
     */
    @Builder
    public record ChallengeFullDto(UUID id, String name, String description, int target,
                                   List<SessionFullDto> sessions, String type,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {

        public ChallengeFullDto(ChallengeEntity challengeEntity) {
            this(challengeEntity.getId(), challengeEntity.getName(), challengeEntity.getDescription(),
                    challengeEntity.getTarget(), getMinimalSessions(challengeEntity.getSessions()),
                    challengeEntity.getType().name(), challengeEntity.getCreatedAt(), challengeEntity.getUpdatedAt());
        }

        public static ChallengeFullDto justMinimal (ChallengeEntity challengeEntity) {
            return new ChallengeFullDto(challengeEntity.getId(), challengeEntity.getName(),
                    challengeEntity.getDescription(), challengeEntity.getTarget(), null,
                    challengeEntity.getType().name(), challengeEntity.getCreatedAt(), challengeEntity.getUpdatedAt());
        }
    }

    @Builder
    public record MinimalChallengeDto(UUID id, String name, String description, int target,
                                   String type, LocalDateTime createdAt, LocalDateTime updatedAt) {

        public MinimalChallengeDto(ChallengeEntity challengeEntity) {
            this(challengeEntity.getId(), challengeEntity.getName(), challengeEntity.getDescription(), challengeEntity.getTarget(),
                    challengeEntity.getType().name(), challengeEntity.getCreatedAt(), challengeEntity.getUpdatedAt());
        }
    }


    @Builder
    public record ChallengeCreateDto(String name, String description, int target, String type, UUID[] sessions) {
    }

    public static List<ChallengeFullDto> getMinimalChallenges(List<ChallengeEntity> challengeEntities) {
        List<ChallengeFullDto> challengeFullDtos = new ArrayList<>();
        challengeEntities.forEach(challenge -> challengeFullDtos.add(ChallengeFullDto.justMinimal(challenge)));
        return challengeFullDtos;
    }

    @Builder
    public record Challenge(UUID id, String name, String description, int target,
                                    List<Session> sessions, List<Subscription> subscriptions, String type,
                            LocalDateTime createdAt, LocalDateTime updatedAt, UUID createdBy, UUID updatedBy) implements Domain {

        @Override
        public String alternateName() {
            return "";
        }

        public static Challenge justMinimal (ChallengeEntity challengeEntity) {
            return new Challenge(challengeEntity.getId(), challengeEntity.getName(), challengeEntity.getDescription(), challengeEntity.getTarget(),
                    null, null, challengeEntity.getType().name(), challengeEntity.getCreatedAt(),
                    challengeEntity.getUpdatedAt(), challengeEntity.getCreatedBy(), challengeEntity.getUpdatedBy());
        }


    }

    /**
     * SubscriptionEntity Helper DTO
     */
    @Builder
    public record SubscriptionFullDto(UUID id, int target, boolean blocked, UserFullDto user, ChallengeFullDto challenge,
                                      SessionFullDto session, LocalDateTime createdAt, LocalDateTime updatedAt) {
        public SubscriptionFullDto(SubscriptionEntity subscriptionEntity) {
            this(subscriptionEntity.getId(), subscriptionEntity.getTarget(), subscriptionEntity.isBlocked(), new UserFullDto(subscriptionEntity.getUser()),
                    ChallengeFullDto.justMinimal(subscriptionEntity.getChallenge()), SessionFullDto.justMinimal(subscriptionEntity.getSession()), subscriptionEntity.getCreatedAt(),
                    subscriptionEntity.getUpdatedAt());
        }

        public static SubscriptionFullDto justMinimal (SubscriptionEntity subscriptionEntity) {
            return new SubscriptionFullDto(subscriptionEntity.getId(), subscriptionEntity.getTarget(), subscriptionEntity.isBlocked(), null,
                    null, null,  subscriptionEntity.getCreatedAt(),
                    subscriptionEntity.getUpdatedAt());
        }
    }


    @Builder
    public record SubscriptionCreateDto(int target, UUID challengeId) {
//        public SubscriptionCreateDto withUserId(UUID userId) {
//            return new SubscriptionCreateDto(target, userId, challengeId, sessionId);
//        }

    }

    public static List<SubscriptionFullDto> getMinimalSubscriptions(List<SubscriptionEntity> subscriptionEntities) {
        if (Objects.nonNull(subscriptionEntities)) {
            List<SubscriptionFullDto> subscriptionFullDtos = new ArrayList<>();
            subscriptionEntities.forEach(subscription -> subscriptionFullDtos.add(SubscriptionFullDto.justMinimal(subscription)));
            return subscriptionFullDtos;
        }
        return null;
    }


    @Builder
    public record Subscription(UUID id, int target, boolean blocked, User user, Challenge challenge,
                                      Session session, List<ChallengeReport> reports,
                               LocalDateTime createdAt, LocalDateTime updatedAt, UUID createdBy, UUID updatedBy) implements Domain {

        @Override
        public String name() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public String alternateName() {
            return "";
        }



        public static Subscription justMinimal (SubscriptionEntity subscriptionEntity) {
            return new Subscription(subscriptionEntity.getId(), subscriptionEntity.getTarget(), subscriptionEntity.isBlocked(), null,
                    null, null, null,  subscriptionEntity.getCreatedAt(),
                    subscriptionEntity.getUpdatedAt(), subscriptionEntity.getCreatedBy(), subscriptionEntity.getUpdatedBy());
        }
    }



    /**
     * ChallengeReportEntity Helper DTO
     */
    @Builder
    public record ChallengeReportCreateDto(int numberEvangelizedTo, int numberOfNewConverts,
                                           int numberFollowedUp, String difficulties, String remark) {
    }

    @Builder
    public record ChallengeReportFullDto(UUID id, SubscriptionEntity subscriptionEntity, int numberEvangelizedTo,
                                         int numberOfNewConverts, int numberFollowedUp, String difficulties,
                                         String remark, LocalDateTime createdAt, LocalDateTime updatedAt) {
        public ChallengeReportFullDto(ChallengeReportEntity challengeReportEntity) {
            this(challengeReportEntity.getId(), challengeReportEntity.getSubscription(), challengeReportEntity.getNumberEvangelizedTo(),
                    challengeReportEntity.getNumberOfNewConverts(), challengeReportEntity.getNumberFollowedUp(),
                    challengeReportEntity.getDifficulties(), challengeReportEntity.getRemark(), challengeReportEntity.getCreatedAt(),
                    challengeReportEntity.getUpdatedAt());
        }

        public static ChallengeReportFullDto justMinimal(ChallengeReportEntity challengeReportEntity) {
            return new ChallengeReportFullDto(challengeReportEntity.getId(), null, challengeReportEntity.getNumberEvangelizedTo(),
                    challengeReportEntity.getNumberOfNewConverts(), challengeReportEntity.getNumberFollowedUp(),
                    challengeReportEntity.getDifficulties(), challengeReportEntity.getRemark(), challengeReportEntity.getCreatedAt(),
                    challengeReportEntity.getUpdatedAt());
        }
    }

    public static List<ChallengeReportFullDto> getMinimalChallengeReport(List<ChallengeReportEntity> reports) {
        if(Objects.nonNull(reports)) {
            List<ChallengeReportFullDto> challengeReportFullDtos = new ArrayList<>();
            reports.forEach(report -> challengeReportFullDtos.add(ChallengeReportFullDto.justMinimal(report)));
            return challengeReportFullDtos;
        }
        return null;
    }

    public record RequestProps(UUID id, String status, String type, String role, UUID[] ids, boolean blocked, UUID challengeId) {
    }


    @Builder
    public record ChallengeReport(UUID id, SubscriptionEntity subscription, int numberEvangelizedTo,
                                         int numberOfNewConverts, int numberFollowedUp, String difficulties,
                                         String remark, LocalDateTime createdAt, LocalDateTime updatedAt, UUID createdBy, UUID updatedBy) implements Domain {


        public static ChallengeReport justMinimal(ChallengeReportEntity challengeReportEntity) {
            return new ChallengeReport(challengeReportEntity.getId(), null, challengeReportEntity.getNumberEvangelizedTo(),
                    challengeReportEntity.getNumberOfNewConverts(), challengeReportEntity.getNumberFollowedUp(),
                    challengeReportEntity.getDifficulties(), challengeReportEntity.getRemark(), challengeReportEntity.getCreatedAt(),
                    challengeReportEntity.getUpdatedAt(), challengeReportEntity.getCreatedBy(), challengeReportEntity.getUpdatedBy());
        }

        @Override
        public String name() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public String alternateName() {
            return "";
        }
    }
}
