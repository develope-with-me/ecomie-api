package org.csbf.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Service
@RequiredArgsConstructor
public class AuthContext {

    public Authentication getAuthUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    public void setAuthUser(UsernamePasswordAuthenticationToken authToken) {
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
