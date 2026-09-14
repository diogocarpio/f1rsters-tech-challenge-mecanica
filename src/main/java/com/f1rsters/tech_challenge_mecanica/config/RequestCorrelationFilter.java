package com.f1rsters.tech_challenge_mecanica.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestCorrelationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestCorrelationFilter.class);
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        String correlationId = resolveCorrelationId(request);
        
        MDC.put("request_id", requestId);
        MDC.put("correlation_id", correlationId);
        MDC.put("request_method", request.getMethod());
        MDC.put("request_uri", request.getRequestURI());
        MDC.put("remote_addr", request.getRemoteAddr());
        
        // Adicionar headers de correlação New Relic se existirem
        String traceId = request.getHeader("traceparent");
        if (traceId != null) {
            MDC.put("trace_id", traceId);
        }
        
        response.setHeader(REQUEST_ID_HEADER, requestId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            log.info("Requisição recebida: {} {}", request.getMethod(), request.getRequestURI());
            
            filterChain.doFilter(request, response);
            
            MDC.put("response_status", String.valueOf(response.getStatus()));
            log.info("Requisição concluída: status={}", response.getStatus());
            
        } finally {
            MDC.clear();
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String headerValue = request.getHeader(REQUEST_ID_HEADER);
        if (headerValue == null || headerValue.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return headerValue;
    }
    
    private String resolveCorrelationId(HttpServletRequest request) {
        String headerValue = request.getHeader(CORRELATION_ID_HEADER);
        if (headerValue == null || headerValue.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return headerValue;
    }
}
