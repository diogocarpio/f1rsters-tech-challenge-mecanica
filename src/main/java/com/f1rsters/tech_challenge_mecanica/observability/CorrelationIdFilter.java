package com.f1rsters.tech_challenge_mecanica.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-Correlation-ID";
    public static final String MDC_KEY = "correlation_id";
    private static final int MAX_CORRELATION_ID_LENGTH = 128;
    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = resolveCorrelationId(request.getHeader(HEADER_NAME));
        long startedAt = System.nanoTime();
        MDC.put(MDC_KEY, correlationId);
        response.setHeader(HEADER_NAME, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMillis = (System.nanoTime() - startedAt) / 1_000_000;
            LOGGER.atInfo()
                    .addKeyValue("http_method", request.getMethod())
                    .addKeyValue("http_path", request.getRequestURI())
                    .addKeyValue("http_status", response.getStatus())
                    .addKeyValue("duration_ms", durationMillis)
                    .log("http_request_completed");
            MDC.remove(MDC_KEY);
        }
    }

    private String resolveCorrelationId(String value) {
        if (value == null || value.isBlank() || value.length() > MAX_CORRELATION_ID_LENGTH) {
            return UUID.randomUUID().toString();
        }
        return value;
    }
}
