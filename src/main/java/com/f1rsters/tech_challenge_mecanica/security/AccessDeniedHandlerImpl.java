package com.f1rsters.tech_challenge_mecanica.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{" +
                "\"timestamp\":\"" + Instant.now() + "\"," +
                "\"status\":" + HttpServletResponse.SC_FORBIDDEN + "," +
                "\"error\":\"Forbidden\"," +
                "\"message\":\"Usuario autenticado sem permissao para este recurso\"," +
                "\"path\":\"" + request.getRequestURI() + "\"" +
                "}");
    }
}

