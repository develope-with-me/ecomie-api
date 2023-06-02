package org.csbf.security.utils.helperclasses;

import lombok.Builder;

import java.util.UUID;

public class HelperDto {

    @Builder
    public record RegisterRequest(String firstname, String lastname, String email, String password) {
    }

    @Builder
    public record AuthenticationRequest(String email, String password) {
    }

    @Builder
    public record AuthenticationResponse(String token, String message, boolean success, HelperDto.UserDto user) {
        public AuthenticationResponse(String token, String message, boolean success) {
            this(token, message, success, null);
        }
    }

    @Builder
    public record ConfirmEmailResponse(String token, ResponseMessage responseMessage) {

    }

    @Builder
    public record UserDto(UUID id, String firstname, String lastname, String email, boolean accountEnabled,
                          boolean accountBlocked) {
    }

    public record ResendVerificationEmailDTO(String email) {
    }


}
