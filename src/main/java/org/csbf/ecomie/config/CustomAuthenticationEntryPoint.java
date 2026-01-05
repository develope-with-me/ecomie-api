package org.csbf.ecomie.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.csbf.ecomie.exceptions.Problems;
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
         ObjectMapper objectMapper = new ObjectMapper();
         response.getWriter().write(objectMapper.writeValueAsString(Problems.UNAUTHORIZED));
    }
}
