package com.f1rsters.tech_challenge_mecanica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1rsters.tech_challenge_mecanica.dto.LoginRequestDTO;
import com.f1rsters.tech_challenge_mecanica.security.AccessDeniedHandlerImpl;
import com.f1rsters.tech_challenge_mecanica.security.AuthEntryPoint;
import com.f1rsters.tech_challenge_mecanica.security.CustomUserDetailsService;
import com.f1rsters.tech_challenge_mecanica.security.JwtAuthenticationFilter;
import com.f1rsters.tech_challenge_mecanica.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthEntryPoint authEntryPoint;

    @MockitoBean
    private AccessDeniedHandlerImpl accessDeniedHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginRequestDTO request = new LoginRequestDTO();
        request.email = "admin@teste.com";
        request.senha = "123456";

        User user = new User(
                "admin@teste.com",
                "123456",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(Objects.requireNonNull(authentication.getPrincipal())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("fake-jwt-token");
        when(jwtService.getExpirationInSeconds()).thenReturn(3600L);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }
}

