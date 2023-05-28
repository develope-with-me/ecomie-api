package org.csbf.security.utils.helperclasses;

import lombok.Builder;
import org.csbf.security.utils.UserDTO;

public class HelperDto {

    @Builder
    public record RegisterRequest (String firstname, String lastname, String email, String password){
    }
    @Builder
    public record AuthenticationRequest (String email, String password){
    }

    @Builder
    public record AuthenticationResponse (String token, String message, boolean success, UserDto user){

    }
    @Builder
    public record UserDto(Integer id, String firstname, String lastname, String email, boolean accountEnabled, boolean accountBlocked) {
    }

    public record ResendVerificationEmailDTO(String email) {
    }


}
