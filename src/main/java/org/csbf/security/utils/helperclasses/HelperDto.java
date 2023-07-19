package org.csbf.security.utils.helperclasses;

import jakarta.persistence.*;
import lombok.Builder;
import org.csbf.security.model.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class HelperDto {

    @Builder
    public record RegisterRequest(String firstname, String lastname, String email, String password) {
    }

    @Builder
    public record AuthenticationRequest(String email, String password) {
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
    public record UserBasicDto(UUID id, String firstname, String lastname, String email, boolean accountEnabled, boolean accountBlocked, boolean accountSoftDeleted) {
    }

    @Builder
    public record UserDto(String firstname, String lastname, String email, String phoneNumber, String country, String region, String city, String language, String profilePictureFileName) {
        public UserDto(User user) {
this(user.getFirstname(), user.getLastname(), user.getEmail(), user.getPhoneNumber(), user.getCountry(), user.getRegion(), user.getCity(), user.getLanguage(), user.getProfilePictureFileName());
        }
    }

    @Builder
    public record UserFullDto(UUID id, String firstname, String lastname, String email, String phoneNumber, String country, String region, String city, String language, String profilePictureFileName, boolean accountEnabled, boolean accountBlocked, boolean accountSoftDeleted) {
        public UserFullDto(User user) {
            this(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getPhoneNumber(), user.getCountry(), user.getRegion(), user.getCity(), user.getLanguage(), user.getProfilePictureFileName(), user.isAccountEnabled(), user.isAccountBlocked(), user.isAccountSoftDeleted());
        }
    }


    public record ResendVerificationEmailDTO(String email) {
    }

    public record UpdateUserProfileRequest(String firstname, String lastname, String phoneNumber, String country, String region, String city, String language){
    }

    public record EmailRequest(String email) {
    }


    /** Session Helper DTO */
    @Builder
    public record SessionFullDto(UUID id, String name, String description, List<ChallengeReport> challengeReports, List<Challenge> challenges, List<Subscription> subscriptions, LocalDateTime startDate, LocalDateTime endDate, String status, LocalDateTime createdAt, LocalDateTime updatedAt){}

    @Builder
    public record SessionCreateDto(String name, String description, LocalDateTime startDate, LocalDateTime endDate){}


    /** Challenge Helper DTO */
    @Builder
    public record ChallengeFullDto(UUID id, String name, String description, long target, List<Subscription> subscriptions, List<ChallengeReport> challengeReports, List<Session> sessions, String type, LocalDateTime createdAt, LocalDateTime updatedAt) {

    }

    @Builder
    public record ChallengeCreateDto(String name, String description, long target, List<Session> sessions, String type) {

    }
}
