package org.csbf.security.utils.helperclasses;

import lombok.Builder;
import org.csbf.security.model.User;

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


}
