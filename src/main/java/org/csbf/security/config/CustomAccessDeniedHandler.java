package org.csbf.security.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.csbf.security.exceptions.Problems;
import org.csbf.security.utils.commons.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Component("customAccessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(Problems.FORBIDDEN_OPERATION_ERROR.statusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(Mapper.toJsonString(Problems.FORBIDDEN_OPERATION_ERROR));
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"" + accessDeniedException.getMessage() + "\"}");
    }
}