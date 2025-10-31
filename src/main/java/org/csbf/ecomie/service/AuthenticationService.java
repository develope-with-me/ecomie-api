package org.csbf.ecomie.service;

import org.csbf.ecomie.entity.UserTokenEntity;
import org.csbf.ecomie.utils.helperclasses.HelperDomain.*;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    ConfirmEmailResponse confirmEmail(String email, String token);

    ConfirmEmailResponse resetPassword(PasswordDTO passwordDTO, String email,String token);
}
