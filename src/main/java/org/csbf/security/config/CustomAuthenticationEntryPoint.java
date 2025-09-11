package org.csbf.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.csbf.security.exceptions.Problems;
import org.csbf.security.utils.commons.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Component("customAuthenticationEntryPoint")
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
         response.setStatus(Problems.UNAUTHORIZED.statusCode());
         response.setContentType(MediaType.APPLICATION_JSON_VALUE);
         response.getWriter().write(Mapper.toJsonString(Problems.UNAUTHORIZED));

//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
    }
}