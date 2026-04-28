package com.f1rsters.tech_challenge_mecanica.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Test
    void shouldSkipFilterWhenNoAuthHeader() throws ServletException, IOException {
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        
        filter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void shouldSkipFilterWhenInvalidAuthHeader() throws ServletException, IOException {
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Invalid token");
        
        filter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void shouldAuthenticateWhenValidToken() throws ServletException, IOException {
        JwtService jwtService = mock(JwtService.class);
        CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        
        UserDetails userDetails = User.withUsername("test@example.com").password("password").build();
        
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer validToken");
        when(jwtService.extractUsername("validToken")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("validToken", userDetails)).thenReturn(true);
        
        filter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
    }
}
