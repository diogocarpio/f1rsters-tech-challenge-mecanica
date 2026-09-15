package com.f1rsters.tech_challenge_mecanica.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestCorrelationFilterTest {

    private final RequestCorrelationFilter filter = new RequestCorrelationFilter();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void deveGerarIdentificadoresQuandoHeadersNaoForemEnviados() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/clientes/me");
        doAnswer(invocation -> {
            assertNotNull(MDC.get("request_id"));
            assertNotNull(MDC.get("correlation_id"));
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        ArgumentCaptor<String> requestIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> correlationIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(RequestCorrelationFilter.REQUEST_ID_HEADER), requestIdCaptor.capture());
        verify(response).setHeader(eq(RequestCorrelationFilter.CORRELATION_ID_HEADER), correlationIdCaptor.capture());
        assertNotNull(requestIdCaptor.getValue());
        assertNotNull(correlationIdCaptor.getValue());
        assertNull(MDC.get("request_id"));
        assertNull(MDC.get("correlation_id"));
    }

    @Test
    void devePreservarIdentificadoresRecebidos() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(RequestCorrelationFilter.REQUEST_ID_HEADER)).thenReturn("request-123");
        when(request.getHeader(RequestCorrelationFilter.CORRELATION_ID_HEADER)).thenReturn("correlation-123");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/clientes/me");
        doAnswer(invocation -> {
            assertEquals("request-123", MDC.get("request_id"));
            assertEquals("correlation-123", MDC.get("correlation_id"));
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(RequestCorrelationFilter.REQUEST_ID_HEADER, "request-123");
        verify(response).setHeader(RequestCorrelationFilter.CORRELATION_ID_HEADER, "correlation-123");
        assertNull(MDC.get("request_id"));
        assertNull(MDC.get("correlation_id"));
    }
}
