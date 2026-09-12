package com.f1rsters.tech_challenge_mecanica.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void deveGerarCorrelationIdQuandoHeaderNaoForEnviado() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(CorrelationIdFilter.HEADER_NAME)).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/clientes/me");
        doAnswer(invocation -> {
            assertNotNull(MDC.get(CorrelationIdFilter.MDC_KEY));
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        ArgumentCaptor<String> correlationIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(CorrelationIdFilter.HEADER_NAME), correlationIdCaptor.capture());
        assertNotNull(correlationIdCaptor.getValue());
        assertFalse(MDC.getCopyOfContextMap() != null
                && MDC.getCopyOfContextMap().containsKey(CorrelationIdFilter.MDC_KEY));
    }

    @Test
    void devePreservarCorrelationIdRecebido() throws Exception {
        String correlationId = "request-123";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader(CorrelationIdFilter.HEADER_NAME)).thenReturn(correlationId);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/clientes/me");
        doAnswer(invocation -> {
            assertEquals(correlationId, MDC.get(CorrelationIdFilter.MDC_KEY));
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(CorrelationIdFilter.HEADER_NAME, correlationId);
        assertFalse(MDC.getCopyOfContextMap() != null
                && MDC.getCopyOfContextMap().containsKey(CorrelationIdFilter.MDC_KEY));
    }

}
