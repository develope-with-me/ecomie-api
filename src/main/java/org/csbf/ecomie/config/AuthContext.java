package org.csbf.ecomie.config;

import lombok.RequiredArgsConstructor;
import org.csbf.ecomie.constant.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


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
    public boolean isAuthorized(Role role) {
        return this.getAuthUser().getAuthorities().stream().anyMatch(authority -> authority.getAuthority().contains(role.name()));
    }
}
